package com.monta.ocpp.emulator.v201

import com.monta.library.ocpp.common.session.OcppSessionRepository
import com.monta.library.ocpp.common.transport.OcppSettings
import com.monta.library.ocpp.v201.client.OcppClientV201Builder
import com.monta.ocpp.emulator.v201.service.ocpp.OcppClientEventsHandler
import org.koin.dsl.module

object MainModule {
    val module = module {
        single {
            OcppSessionRepository()
        }
        single {
            val ocppClientEventsHandler: OcppClientEventsHandler = get()
            OcppClientV201Builder()
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
                .localMode(get())
                .build()
        }
    }
}
