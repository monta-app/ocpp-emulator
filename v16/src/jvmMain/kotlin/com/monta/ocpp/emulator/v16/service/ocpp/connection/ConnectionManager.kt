package com.monta.ocpp.emulator.v16.service.ocpp.connection

import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.service.interceptor.MessageInterceptor
import com.monta.ocpp.emulator.v16.service.ocpp.SchedulerService
import kotlinx.coroutines.joinAll
import mu.KotlinLogging
import org.koin.core.annotation.Singleton

@Singleton
class ConnectionManager(
    val messageInterceptor: MessageInterceptor
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
        chargePointConnections.map { (chargePointId, chargePointConnection) ->
            launchThread {
                logger.info("Disconnecting chargePointId=$chargePointId")
                getSchedulingService(chargePointId)?.stop()
                chargePointConnection?.disconnect()
            }
        }.joinAll()
    }

    fun disconnect(
        chargePointId: Long
    ) {
        chargePointConnections[chargePointId]?.let { chargePointConnection ->
            logger.info("Disconnecting chargePointId=$chargePointId")
            launchThread {
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
