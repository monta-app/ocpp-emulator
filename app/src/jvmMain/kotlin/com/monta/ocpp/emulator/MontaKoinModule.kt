package com.monta.ocpp.emulator

import com.monta.library.ocpp.common.session.OcppSessionRepository
import com.monta.library.ocpp.common.transport.OcppSettings
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.client.OcppClientV16Builder
import com.monta.library.ocpp.v16.core.CoreClientProfile
import com.monta.library.ocpp.v16.firmware.FirmwareManagementClientProfile
import com.monta.library.ocpp.v16.localauth.LocalListClientProfile
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageClientProfile
import com.monta.library.ocpp.v16.security.SecurityClientProfile
import com.monta.library.ocpp.v16.smartcharge.SmartChargeClientProfile
import com.monta.ocpp.emulator.interceptor.MessageInterceptor
import com.monta.ocpp.emulator.v16.profile.OcppClientEventsHandler
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.monta.ocpp.emulator")
class MontaKoinModule {

    @Single
    fun ocppSessionRepository(): OcppSessionRepository {
        return OcppSessionRepository()
    }

    @Single
    fun ocppClientV16(
        ocppClientEventsHandler: OcppClientEventsHandler,
        interceptor: MessageInterceptor,
        ocppSessionRepository: OcppSessionRepository,
        coreListener: CoreClientProfile.Listener,
        triggerMessageListener: TriggerMessageClientProfile.Listener,
        localAuthListener: LocalListClientProfile.Listener,
        smartChargeListener: SmartChargeClientProfile.Listener,
        firmwareManagementListener: FirmwareManagementClientProfile.Listener,
        securityListener: SecurityClientProfile.Listener,
    ): OcppClientV16 {
        return OcppClientV16Builder()
            .settings(
                OcppSettings(
                    nanoSecondDates = false,
                ),
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
            .localMode(ocppSessionRepository)
            .addCore(coreListener)
            .addTriggerMessage(triggerMessageListener)
            .addLocalAuth(localAuthListener)
            .addSmartCharge(smartChargeListener)
            .addFirmwareManagement(firmwareManagementListener)
            .addSecurity(securityListener)
            .build()
    }
}
