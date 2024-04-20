package com.monta.ocpp.emulator.v16

import com.monta.library.ocpp.common.session.OcppSessionRepository
import com.monta.library.ocpp.common.transport.OcppSettings
import com.monta.library.ocpp.v16.client.OcppClientV16Builder
import com.monta.ocpp.emulator.v16.service.interceptor.MessageInterceptor
import com.monta.ocpp.emulator.v16.service.ocpp.profile.OcppClientEventsHandler
import org.koin.dsl.module

object MainModule {
    val module = module {
        single {
            OcppSessionRepository()
        }
        single {
            val ocppClientEventsHandler: OcppClientEventsHandler = get()
            val interceptor: MessageInterceptor = get()
            OcppClientV16Builder()
                .settings(
                    OcppSettings(
                        nanoSecondDates = false
                    )
                )
                .onConnect { ocppSessionInfo, reconnecting ->
                    ocppClientEventsHandler.onConnect(ocppSessionInfo, reconnecting)
                }
                .onDisconnect { ocppSessionInfo ->
                    ocppClientEventsHandler.onDisconnect(ocppSessionInfo)
                }
                .addSendHook { chargePointIdentity, message ->
                    interceptor.intercept(chargePointIdentity, message)
                }
                .localMode(get())
                .addCore(get())
                .addTriggerMessage(get())
                .addLocalAuth(get())
                .addSmartCharge(get())
                .addFirmwareManagement(get())
                .addSecurity(get())
                .build()
        }
    }
}
