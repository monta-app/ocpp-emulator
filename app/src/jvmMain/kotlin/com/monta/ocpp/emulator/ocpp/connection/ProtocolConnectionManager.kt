package com.monta.ocpp.emulator.ocpp.connection

import com.monta.ocpp.emulator.chargepoint.core.model.OcppVersion
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import kotlinx.coroutines.Job
import javax.inject.Singleton
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager as V16ConnectionManager
import com.monta.ocpp.emulator.ocpp.v201.connection.ConnectionManager as V201ConnectionManager
import com.monta.ocpp.emulator.ocpp.v21.connection.ConnectionManager as V21ConnectionManager

/**
 * Routes connect/disconnect by the charge point's configured OCPP version.
 */
@Singleton
class ProtocolConnectionManager(
    private val chargePointService: ChargePointService,
    private val v16ConnectionManager: V16ConnectionManager,
    private val v201ConnectionManager: V201ConnectionManager,
    private val v21ConnectionManager: V21ConnectionManager,
) {

    fun connect(
        chargePointId: Long,
    ) {
        when (chargePointService.getById(chargePointId).ocppVersion) {
            OcppVersion.V16 -> v16ConnectionManager.connect(chargePointId)
            OcppVersion.V201 -> v201ConnectionManager.connect(chargePointId)
            OcppVersion.V21 -> v21ConnectionManager.connect(chargePointId)
        }
    }

    fun disconnect(
        chargePointId: Long,
    ): Job? {
        return when (chargePointService.getById(chargePointId).ocppVersion) {
            OcppVersion.V16 -> v16ConnectionManager.disconnect(chargePointId)
            OcppVersion.V201 -> v201ConnectionManager.disconnect(chargePointId)
            OcppVersion.V21 -> v21ConnectionManager.disconnect(chargePointId)
        }
    }

    suspend fun disconnectAll() {
        v16ConnectionManager.disconnectAll()
        v201ConnectionManager.disconnectAll()
        v21ConnectionManager.disconnectAll()
    }

    fun reconnect(
        chargePointId: Long,
        delayInSeconds: Int,
    ) {
        when (chargePointService.getById(chargePointId).ocppVersion) {
            OcppVersion.V16 -> v16ConnectionManager.reconnect(chargePointId, delayInSeconds)
            OcppVersion.V201 -> v201ConnectionManager.reconnect(chargePointId, delayInSeconds)
            OcppVersion.V21 -> v21ConnectionManager.reconnect(chargePointId, delayInSeconds)
        }
    }
}
