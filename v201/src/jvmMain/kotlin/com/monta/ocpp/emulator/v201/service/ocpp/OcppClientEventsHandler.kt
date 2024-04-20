package com.monta.ocpp.emulator.v201.service.ocpp

import com.monta.library.ocpp.common.session.OcppSession
import org.koin.core.annotation.Singleton

@Singleton
class OcppClientEventsHandler {

    suspend fun onConnect(
        ocppSessionInfo: OcppSession.Info,
        isReconnecting: Boolean
    ) {
        if (isReconnecting) {
            return
        }
    }

    suspend fun onDisconnect(
        ocppSessionInfo: OcppSession.Info
    ): Boolean {
        return true
    }
}
