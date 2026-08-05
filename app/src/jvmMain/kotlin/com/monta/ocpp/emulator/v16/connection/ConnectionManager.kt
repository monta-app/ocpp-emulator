package com.monta.ocpp.emulator.v16.connection

import com.monta.ocpp.emulator.chargepoint.repository.ChargePointRepository
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.interceptor.MessageInterceptor
import com.monta.ocpp.emulator.v16.SchedulerService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

@Singleton
class ConnectionManager(
    val messageInterceptor: MessageInterceptor,
    val chargePointRepository: ChargePointRepository,
) {

    private val logger = KotlinLogging.logger {}

    private val chargePointSchedulers = ConcurrentHashMap<Long, SchedulerService>()
    private val chargePointConnections = ConcurrentHashMap<Long, ChargePointConnection>()

    fun connect(
        chargePointId: Long,
    ) {
        // Claim the charge point on the calling thread, the persisted connected flag is false
        // for the whole of every reconnect backoff so it can't guard this
        val chargePointConnection = chargePointConnections.computeIfAbsent(chargePointId) {
            ChargePointConnection(it)
        }

        val started = chargePointConnection.start {
            logger.info { "Connecting chargePointId=$chargePointId" }
            chargePointRepository.clearChargePointBootStatus(chargePointId)
            chargePointConnection.connect()
        }

        if (!started) {
            return
        }

        getSchedulingService(chargePointId, true)?.start()

        if (messageInterceptor.messageTypeConfig[chargePointId] == null) {
            messageInterceptor.addDefaults(chargePointId)
        }
    }

    suspend fun disconnectAll() {
        chargePointConnections.keys.mapNotNull { chargePointId ->
            disconnect(chargePointId)
        }.joinAll()
    }

    fun disconnect(
        chargePointId: Long,
    ): Job? {
        // Release the claim on the calling thread so a later connect() is admitted
        return chargePointConnections.remove(chargePointId)?.let { chargePointConnection ->
            logger.info { "Disconnecting chargePointId=$chargePointId" }
            launchThread {
                chargePointRepository.clearChargePointBootStatus(chargePointId)
                getSchedulingService(chargePointId)?.stop()
                chargePointConnection.stop()
            }
        }
    }

    fun reconnect(
        chargePointId: Long,
        delayInSeconds: Int,
    ) {
        chargePointConnections[chargePointId]?.let { chargePointConnection ->
            logger.info { "Reconnecting chargePointId=$chargePointId" }
            chargePointConnection.restart(delayInSeconds)
        }
    }

    private fun getSchedulingService(
        chargePointId: Long,
        create: Boolean = false,
    ): SchedulerService? {
        return if (create) {
            chargePointSchedulers.computeIfAbsent(chargePointId) {
                SchedulerService(it)
            }
        } else {
            chargePointSchedulers[chargePointId]
        }
    }
}
