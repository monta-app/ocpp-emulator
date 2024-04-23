package com.monta.ocpp.emulator.v16.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.logger.GlobalLogger
import com.monta.ocpp.emulator.v16.ChargePointManager
import org.koin.core.annotation.Singleton

@Singleton
class OcppClientEventsHandler {

    private val chargePointService: ChargePointService by injectAnywhere()
    private val chargePointManager: ChargePointManager by injectAnywhere()

    suspend fun onConnect(
        ocppSessionInfo: OcppSession.Info,
        isReconnecting: Boolean
    ) {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        GlobalLogger.info(chargePoint, "Connected")

        if (isReconnecting) {
            return
        }
        launchThread {
            chargePointManager.startBootSequence(chargePoint)
        }
    }

    suspend fun onDisconnect(
        ocppSessionInfo: OcppSession.Info
    ): Boolean {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        GlobalLogger.info(chargePoint, "Disconnected")
        return true
    }
}
