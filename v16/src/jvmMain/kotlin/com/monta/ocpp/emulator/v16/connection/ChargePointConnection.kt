package com.monta.ocpp.emulator.v16.connection

import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.StatusNotificationRequest
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.MontaSerialization
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.interceptor.MessageInterceptor
import com.monta.ocpp.emulator.logger.GlobalLogger
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class ChargePointConnection(
    var chargePointId: Long
) {

    private val logger = KotlinLogging.logger {}

    private val chargePointService: ChargePointService by injectAnywhere()
    private val ocppClientV16: OcppClientV16 by injectAnywhere()
    private val interceptor: MessageInterceptor by injectAnywhere()

    private val reconnect = AtomicBoolean(true)
    private var websocketSession: WebSocketSession? = null
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
    }
    private var connectionAttempts = 1

    private val requestIdMap = ConcurrentMap<String, Long>()
    private val totalLatencyMillis = AtomicLong(0L)
    private val messageCount = AtomicInteger(0)

    val chargePoint: ChargePointDAO
        get() = chargePointService.getById(chargePointId)

    suspend fun connect(
        isReconnecting: Boolean = false
    ) {
        logger.info("Connecting... (isReconnecting=$isReconnecting)")

        try {
            createConnection(isReconnecting)
        } catch (exception: WebSocketException) {
            val isAuthError = exception.message?.contains("401") == true
            // Failed to connect at all, so lets try reconnecting
            handleReconnection(
                isAuthError = isAuthError,
                forceConnect = true,
                additionalInfo = if (!isAuthError) exception.message else null
            )
        } catch (exception: Exception) {
            logger.warn("error connecting", exception)
            // Failed to connect at all, so lets try reconnecting
            handleReconnection(
                isAuthError = false,
                forceConnect = true,
                additionalInfo = null
            )
        }
    }

    private suspend fun createConnection(
        isReconnecting: Boolean
    ) {
        client.webSocket(
            request = {
                // Set basic auth password if needed
                chargePoint.basicAuthPassword?.let { password ->
                    basicAuth(chargePoint.identity, password)
                }
                url("${chargePoint.ocppUrl}/${chargePoint.identity}")
                header("Sec-WebSocket-Protocol", "ocpp1.6")
            }
        ) {
            websocketSession = this

            ocppClientV16.connect(
                identity = chargePoint.identity,
                isReconnecting = isReconnecting,
                sendFrame = { message ->
                    GlobalLogger.logSend(chargePoint, message)
                    this.send(message)
                    logLatency(message)
                },
                closeConnection = { reason ->
                    this.close(CloseReason(CloseReason.Codes.NORMAL, reason))
                }
            )

            // Yes we should automatically reconnect on failure
            reconnect.set(true)
            // Set our connection attempts to 3 (used for the backoff calculation)
            connectionAttempts = 0
            // Set our charge point as connected
            chargePointService.update(chargePoint) {
                this.connected = true
            }

            try {
                for (frame in this.incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val message = frame.readText()
                            val newMessage = interceptor.intercept(chargePoint.identity, message)
                            GlobalLogger.logReceive(chargePoint, message)
                            if (newMessage != null) {
                                ocppClientV16.receiveMessage(chargePoint.identity, newMessage)
                            }
                            logLatency(message)
                        }

                        else -> {
                            GlobalLogger.warn(chargePoint, "unknown frame $frame")
                        }
                    }
                }
            } catch (e: Throwable) {
                GlobalLogger.warn(chargePoint, "onError ${chargePoint.identity} ${closeReason.await()}")
            }

            // Once our connection stuff is done in the above
            // We should try to reconnect if it is needed
            handleReconnection(
                isAuthError = false,
                forceConnect = false,
                additionalInfo = null
            )
        }
    }

    private suspend fun logLatency(
        websocketMessage: String
    ) {
        val jsonNode = MontaSerialization.objectMapper.readTree(websocketMessage)
        val requestId = jsonNode.get(1).asText()
        if (requestIdMap.contains(requestId)) {
            val timestamp = requestIdMap.remove(requestId)
            if (timestamp == null) {
                return
            }
            val latency = System.currentTimeMillis() - timestamp
            totalLatencyMillis.addAndGet(latency)
            messageCount.incrementAndGet()
            val averageLatencyMillis = totalLatencyMillis.get().toDouble() / messageCount.get()
            chargePointService.update(chargePoint) {
                this.averageLatencyMillis = averageLatencyMillis.roundToInt()
            }
        } else {
            requestIdMap[requestId] = System.currentTimeMillis()
        }
    }

    suspend fun disconnect(
        closeReason: CloseReason = CloseReason(
            code = CloseReason.Codes.NORMAL,
            message = ""
        )
    ) {
        val chargePoint = chargePointService.getById(chargePoint.idValue)
        ocppClientV16.sendMessage(
            chargePoint.identity,
            StatusNotificationRequest(
                connectorId = 0,
                errorCode = chargePoint.errorCode,
                info = "Disconnecting",
                status = ChargePointStatus.Unavailable
            )
        )
        chargePointService.update(chargePoint) {
            this.status = ChargePointStatus.Unavailable
            this.connected = false
        }
        reconnect.set(false)
        websocketSession?.close(closeReason)
    }

    suspend fun reconnect(
        delayInSeconds: Int,
        closeReason: CloseReason = CloseReason(CloseReason.Codes.NORMAL, "")
    ) {
        GlobalLogger.info(chargePoint, "Reconnecting after $delayInSeconds seconds")
        disconnect(closeReason)
        delay(delayInSeconds.toLong() * 1000)
        connect(true)
    }

    private suspend fun handleReconnection(
        isAuthError: Boolean,
        forceConnect: Boolean,
        additionalInfo: String?
    ) {
        val backOffTime = getBackoffTime()

        val shouldReconnect = try {
            ocppClientV16.onDisconnect(chargePoint.identity)
        } catch (exception: Exception) {
            true
        }

        websocketSession = null

        // Set our charge point state to disconnected
        chargePointService.update(chargePoint) {
            this.connected = false
        }

        if (forceConnect || (reconnect.get() && shouldReconnect)) {
            val errorMessage = when {
                isAuthError -> "Charge point failed to authenticate"
                additionalInfo != null -> "Unable to connect to server because of $additionalInfo, trying again in ${backOffTime}s"
                else -> "Unable to connect to server, trying again in ${backOffTime}s"
            }
            GlobalLogger.warn(chargePoint, errorMessage)
            delay(backOffTime * 1000L)
            connect(true)
        } else {
            GlobalLogger.warn(chargePoint, "Unable to connect to server, will not attempt to reconnect")
        }
    }

    private fun getBackoffTime(): Int {
        val attempts = connectionAttempts++

        return min(
            a = 60,
            b = max(
                a = 2.0.pow(attempts.toDouble()).roundToInt(),
                b = 1
            )
        )
    }
}
