package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v201.service.ChargePointManager
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.platform.util.launchThread
import javax.inject.Singleton

@Singleton
class OcppClientEventsHandler {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val chargePointManager: ChargePointManager by injectAnywhere()

    suspend fun onConnect(
        ocppSessionInfo: OcppSession.Info,
        isReconnecting: Boolean,
    ) {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        GlobalLogger.info(chargePoint, "Connected (OCPP 2.0.1)")
        if (chargePoint.bootedAt !== null) {
            return
        }
        launchThread {
            chargePointManager.startBootSequence(chargePoint)
        }
    }

    suspend fun onDisconnect(
        ocppSessionInfo: OcppSession.Info,
    ): Boolean {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        GlobalLogger.info(chargePoint, "Disconnected (OCPP 2.0.1)")
        return true
    }
}
