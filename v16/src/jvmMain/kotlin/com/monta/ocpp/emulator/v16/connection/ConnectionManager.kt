package com.monta.ocpp.emulator.v16.connection

import com.monta.ocpp.emulator.chargepoint.repository.ChargePointRepository
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.interceptor.MessageInterceptor
import com.monta.ocpp.emulator.v16.SchedulerService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import javax.inject.Singleton

@Singleton
class ConnectionManager(
    val messageInterceptor: MessageInterceptor,
    val chargePointRepository: ChargePointRepository
) {

    private val logger = KotlinLogging.logger {}

    private val chargePointSchedulers: MutableMap<Long, SchedulerService?> = mutableMapOf()
    private val chargePointConnections: MutableMap<Long, ChargePointConnection?> = mutableMapOf()

    fun connect(
        chargePointId: Long
    ) {
        val isConnected = chargePointConnections[chargePointId]?.chargePoint?.connected

        if (isConnected == true) {
            return
        }

        launchThread {
            logger.info("Connecting chargePointId=$chargePointId")
            //
            chargePointRepository.clearChargePointBootStatus(chargePointId)
            // Create a new connection and add it to the map
            chargePointConnections[chargePointId]?.disconnect()
            chargePointConnections[chargePointId] = null
            chargePointConnections[chargePointId] = ChargePointConnection(chargePointId)
            chargePointConnections[chargePointId]?.connect()
        }

        getSchedulingService(chargePointId, true)?.start()

        if (messageInterceptor.messageTypeConfig[chargePointId] == null) {
            messageInterceptor.addDefaults(chargePointId)
        }
    }

    suspend fun disconnectAll() {
        chargePointConnections.mapNotNull { (chargePointId, _) ->
            disconnect(chargePointId)
        }.joinAll()
    }

    fun disconnect(
        chargePointId: Long
    ): Job? {
        return chargePointConnections[chargePointId]?.let { chargePointConnection ->
            logger.info("Disconnecting chargePointId=$chargePointId")
            launchThread {
                chargePointRepository.clearChargePointBootStatus(chargePointId)
                getSchedulingService(chargePointId)?.stop()
                chargePointConnection.disconnect()
                chargePointConnections.remove(chargePointId)
            }
        }
    }

    fun reconnect(
        chargePointId: Long,
        delayInSeconds: Int
    ) {
        chargePointConnections[chargePointId]?.let { chargePointConnection ->
            launchThread {
                logger.info("Reconnecting chargePointId=$chargePointId")
                chargePointConnection.reconnect(delayInSeconds)
            }
        }
    }

    private fun getSchedulingService(
        chargePointId: Long,
        create: Boolean = false
    ): SchedulerService? {
        return if (create) {
            chargePointSchedulers.getOrPut(chargePointId) {
                SchedulerService(chargePointId)
            }
        } else {
            chargePointSchedulers[chargePointId]
        }
    }
}
