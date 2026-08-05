package com.monta.ocpp.emulator.v16.connection

import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.StatusNotificationRequest
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.MontaSerialization
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.interceptor.MessageInterceptor
import com.monta.ocpp.emulator.logger.GlobalLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

class ChargePointConnection(
    var chargePointId: Long,
) {

    private val logger = KotlinLogging.logger {}

    private val chargePointService: ChargePointService by injectAnywhere()
    private val ocppClientV16: OcppClientV16 by injectAnywhere()
    private val interceptor: MessageInterceptor by injectAnywhere()

    private val reconnect = AtomicBoolean(true)
    private var websocketSession: WebSocketSession? = null
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20.seconds
        }
    }
    private var connectionAttempts = 1

    private val requestIdMap = ConcurrentMap<String, Long>()
    private val totalLatencyNanos = AtomicLong(0L)
    private val messageCount = AtomicInteger(0)

    private val chainLock = Any()

    private var connectionJob: Job? = null
    private var connectedAtNanos: Long? = null

    val chargePoint: ChargePointDAO
        get() = chargePointService.getById(chargePointId)

    suspend fun connect(
        isReconnecting: Boolean = false,
    ) {
        logger.info { "Connecting... (isReconnecting=$isReconnecting)" }

        try {
            createConnection(isReconnecting)
        } catch (exception: WebSocketException) {
            currentCoroutineContext().ensureActive()
            val isAuthError = exception.message?.contains("401") == true
            // Failed to connect at all, so lets try reconnecting
            handleReconnection(
                isAuthError = isAuthError,
                forceConnect = true,
                additionalInfo = if (!isAuthError) exception.message else null,
            )
        } catch (exception: Exception) {
            currentCoroutineContext().ensureActive()
            logger.warn(exception) { "error connecting" }
            // Failed to connect at all, so lets try reconnecting
            handleReconnection(
                isAuthError = false,
                forceConnect = true,
                additionalInfo = null,
            )
        }
    }

    private suspend fun createConnection(
        isReconnecting: Boolean,
    ) {
        client.webSocket(
            request = {
                // Set basic auth password if needed
                chargePoint.basicAuthPassword?.let { password ->
                    basicAuth(chargePoint.identity, password)
                }
                url("${chargePoint.ocppUrl}/${chargePoint.identity}")
                header("Sec-WebSocket-Protocol", "ocpp1.6")
            },
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
                },
            )

            // Yes we should automatically reconnect on failure
            reconnect.set(true)
            // Only reset the backoff once the connection has proven stable
            connectedAtNanos = System.nanoTime()
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
                additionalInfo = null,
            )
        }
    }

    private suspend fun logLatency(
        websocketMessage: String,
    ) {
        val jsonNode = MontaSerialization.objectMapper.readTree(websocketMessage)
        val requestId = jsonNode.get(1).asText()
        if (requestIdMap.contains(requestId)) {
            val timestamp = requestIdMap.remove(requestId)
            if (timestamp == null) {
                return
            }
            val latency = System.nanoTime() - timestamp
            val total = totalLatencyNanos.addAndGet(latency)
            val messages = messageCount.incrementAndGet()
            val averageLatencyMillis = Duration.ofNanos(total / messages).toMillis()
            chargePointService.update(chargePoint) {
                this.messageCount = messages
                this.averageLatencyMillis = averageLatencyMillis
            }
        } else {
            requestIdMap[requestId] = System.nanoTime()
        }
    }

    suspend fun disconnect(
        closeReason: CloseReason = CloseReason(
            code = CloseReason.Codes.NORMAL,
            message = "",
        ),
    ) {
        val chargePoint = chargePointService.getById(chargePoint.idValue)
        try {
            ocppClientV16.sendMessage(
                chargePoint.identity,
                StatusNotificationRequest(
                    connectorId = 0,
                    errorCode = chargePoint.errorCode,
                    info = "Disconnecting",
                    status = ChargePointStatus.Unavailable,
                ),
            )
        } catch (exception: Exception) {
            logger.warn(exception) { "Failed to send disconnect status notification for ${chargePoint.identity}, session may already be closed" }
        }
        chargePointService.update(chargePoint) {
            this.status = ChargePointStatus.Unavailable
            this.connected = false
        }
        reconnect.set(false)
        websocketSession?.close(closeReason)
    }

    // Only one connect/retry chain at a time, the client keys its sessions by identity so a
    // second chain just fights the first one over the same slot
    fun start(
        block: suspend () -> Unit,
    ): Boolean {
        synchronized(chainLock) {
            if (connectionJob?.isActive == true) {
                return false
            }
            connectionJob = launchThread(block = block)
        }
        return true
    }

    suspend fun stop(
        closeReason: CloseReason = CloseReason(CloseReason.Codes.NORMAL, ""),
    ) {
        // Say goodbye first, cancelling closes the socket and the status notification
        // would have nowhere to go
        disconnect(closeReason)
        // A chain already asleep in its backoff never sees the flag disconnect() clears
        synchronized(chainLock) { connectionJob }?.cancelAndJoin()
    }

    fun restart(
        delayInSeconds: Int,
        closeReason: CloseReason = CloseReason(CloseReason.Codes.NORMAL, ""),
    ) {
        synchronized(chainLock) {
            val previous = connectionJob
            connectionJob = launchThread {
                GlobalLogger.info(chargePoint, "Reconnecting after $delayInSeconds seconds")
                // Let the old chain flush what's still in flight and wind itself down,
                // cancelling is just the backstop for when it doesn't
                disconnect(closeReason)
                previous?.cancelAndJoin()
                delay(delayInSeconds.toLong() * 1000)
                connect(true)
            }
        }
    }

    private suspend fun handleReconnection(
        isAuthError: Boolean,
        forceConnect: Boolean,
        additionalInfo: String?,
    ) {
        resetBackoffIfConnectionWasStable()

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

    // A socket that opens and closes straight away isn't a success, only count one that stayed up
    private fun resetBackoffIfConnectionWasStable() {
        val connectedAt = connectedAtNanos ?: return
        connectedAtNanos = null
        if (Duration.ofNanos(System.nanoTime() - connectedAt) >= STABLE_CONNECTION_DURATION) {
            connectionAttempts = 0
        }
    }

    private fun getBackoffTime(): Int {
        val attempts = connectionAttempts++

        return min(
            a = 60,
            b = max(
                a = 2.0.pow(attempts.toDouble()).roundToInt(),
                b = 1,
            ),
        )
    }

    private companion object {
        private val STABLE_CONNECTION_DURATION: Duration = Duration.ofSeconds(30)
    }
}
