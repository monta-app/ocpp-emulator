package com.monta.ocpp.emulator

import com.monta.library.ocpp.common.session.OcppSessionRepository
import com.monta.library.ocpp.common.transport.OcppSettings
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.core.CoreClientProfile
import com.monta.library.ocpp.v16.firmware.FirmwareManagementClientProfile
import com.monta.library.ocpp.v16.localauth.LocalListClientProfile
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageClientProfile
import com.monta.library.ocpp.v16.security.SecurityClientProfile
import com.monta.library.ocpp.v16.smartcharge.SmartChargeClientProfile
import com.monta.library.ocpp.v201.blocks.authorization.AuthorizationClientDispatcher
import com.monta.library.ocpp.v201.blocks.metervalues.MeterValuesClientDispatcher
import com.monta.library.ocpp.v201.blocks.security.SecurityClientDispatcher
import com.monta.library.ocpp.v201.client.OcppClientV201
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor
import com.monta.ocpp.emulator.ocpp.v16.reservation.ReservationClientProfile
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.afrr.AfrrClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.availability.AvailabilityClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.batteryswap.BatterySwapClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.certificatemanagement.CertificateManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.datatransfer.DataTransferClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.der.DerClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.diagnostics.DiagnosticsClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.displaymessage.DisplayMessageClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.dynamicschedule.DynamicScheduleClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.firmwaremanagement.FirmwareManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.localauthorizationlistmanagement.LocalAuthorizationListManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.periodiceventstream.PeriodicEventStreamClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.prioritycharging.PriorityChargingClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.provisioning.ProvisioningClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.remotecontrol.RemoteControlClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.reservation.ReservationClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.smartcharging.SmartChargingClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariff.TariffClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariffandcost.TariffAndCostClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.transactions.TransactionsClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.client.OcppClientV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.client.OcppClientV21Builder
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import com.monta.library.ocpp.v201.blocks.availability.AvailabilityClientDispatcher as AvailabilityClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.certificatemanagement.CertificateManagementClientDispatcher as CertificateManagementClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.datatransfer.DataTransferClientDispatcher as DataTransferClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.diagnostics.DiagnosticsClientDispatcher as DiagnosticsClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.displaymessage.DisplayMessageClientDispatcher as DisplayMessageClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.firmwaremanagement.FirmwareManagementClientDispatcher as FirmwareManagementClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.localauthorizationlistmanagement.LocalAuthorizationListManagementClientDispatcher as LocalAuthorizationListManagementClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.provisioning.ProvisioningClientDispatcher as ProvisioningClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.remotecontrol.RemoteControlClientDispatcher as RemoteControlClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.reservation.ReservationClientDispatcher as ReservationClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.smartcharging.SmartChargingClientDispatcher as SmartChargingClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.tariffandcost.TariffAndCostClientDispatcher as TariffAndCostClientDispatcherV201
import com.monta.library.ocpp.v201.blocks.transactions.TransactionsClientDispatcher as TransactionsClientDispatcherV201
import com.monta.ocpp.emulator.ocpp.v16.profile.OcppClientEventsHandler as OcppClientEventsHandlerV16
import com.monta.ocpp.emulator.ocpp.v201.profile.OcppClientEventsHandler as OcppClientEventsHandlerV201
import com.monta.ocpp.emulator.ocpp.v21.profile.OcppClientEventsHandler as OcppClientEventsHandlerV21

@Module
@ComponentScan("com.monta.ocpp.emulator")
class MontaKoinModule {

    @Single
    fun ocppSessionRepository(): OcppSessionRepository {
        return OcppSessionRepository()
    }

    @Single
    fun ocppClientV16(
        ocppClientEventsHandler: OcppClientEventsHandlerV16,
        interceptor: MessageInterceptor,
        ocppSessionRepository: OcppSessionRepository,
        coreListener: CoreClientProfile.Listener,
        triggerMessageListener: TriggerMessageClientProfile.Listener,
        localAuthListener: LocalListClientProfile.Listener,
        smartChargeListener: SmartChargeClientProfile.Listener,
        firmwareManagementListener: FirmwareManagementClientProfile.Listener,
        securityListener: SecurityClientProfile.Listener,
        reservationListener: ReservationClientProfile.Listener,
    ): OcppClientV16 {
        // Construct directly so we can register the in-repo Reservation profile
        // (OcppClientV16Builder has no addReservation / addProfile).
        return OcppClientV16(
            onConnect = { ocppSessionInfo, reconnecting ->
                ocppClientEventsHandler.onConnect(ocppSessionInfo, reconnecting)
            },
            onDisconnect = { ocppSessionInfo ->
                ocppClientEventsHandler.onDisconnect(ocppSessionInfo)
            },
            ocppSessionRepository = ocppSessionRepository,
            settings = OcppSettings(
                nanoSecondDates = false,
            ),
            profiles = setOf(
                CoreClientProfile(coreListener),
                TriggerMessageClientProfile(triggerMessageListener),
                LocalListClientProfile(localAuthListener),
                SmartChargeClientProfile(smartChargeListener),
                FirmwareManagementClientProfile(firmwareManagementListener),
                SecurityClientProfile(securityListener),
                ReservationClientProfile(reservationListener),
            ),
            sendHook = { chargePointIdentity, message ->
                interceptor.intercept(chargePointIdentity, message)
            },
        )
    }

    @Single
    fun ocppClientV201(
        ocppClientEventsHandler: OcppClientEventsHandlerV201,
        interceptor: MessageInterceptor,
        ocppSessionRepository: OcppSessionRepository,
        provisioningListener: ProvisioningClientDispatcherV201.Listener,
        availabilityListener: AvailabilityClientDispatcherV201.Listener,
        remoteControlListener: RemoteControlClientDispatcherV201.Listener,
        transactionsListener: TransactionsClientDispatcherV201.Listener,
        localAuthListListener: LocalAuthorizationListManagementClientDispatcherV201.Listener,
        smartChargingListener: SmartChargingClientDispatcherV201.Listener,
        reservationListener: ReservationClientDispatcherV201.Listener,
        firmwareManagementListener: FirmwareManagementClientDispatcherV201.Listener,
        certificateManagementListener: CertificateManagementClientDispatcherV201.Listener,
        diagnosticsListener: DiagnosticsClientDispatcherV201.Listener,
        displayMessageListener: DisplayMessageClientDispatcherV201.Listener,
        tariffAndCostListener: TariffAndCostClientDispatcherV201.Listener,
        dataTransferListener: DataTransferClientDispatcherV201.Listener,
    ): OcppClientV201 {
        // Construct directly: OcppClientV201Builder is missing addProvisioning /
        // addAuthorization / addMeterValues / addSecurity.
        return OcppClientV201(
            onConnect = { ocppSessionInfo, reconnecting ->
                ocppClientEventsHandler.onConnect(ocppSessionInfo, reconnecting)
            },
            onDisconnect = { ocppSessionInfo ->
                ocppClientEventsHandler.onDisconnect(ocppSessionInfo)
            },
            ocppSessionRepository = ocppSessionRepository,
            settings = OcppSettings(
                nanoSecondDates = false,
            ),
            profiles = setOf(
                ProvisioningClientDispatcherV201(provisioningListener),
                AuthorizationClientDispatcher(),
                AvailabilityClientDispatcherV201(availabilityListener),
                RemoteControlClientDispatcherV201(remoteControlListener),
                TransactionsClientDispatcherV201(transactionsListener),
                LocalAuthorizationListManagementClientDispatcherV201(localAuthListListener),
                SmartChargingClientDispatcherV201(smartChargingListener),
                ReservationClientDispatcherV201(reservationListener),
                FirmwareManagementClientDispatcherV201(firmwareManagementListener),
                CertificateManagementClientDispatcherV201(certificateManagementListener),
                DiagnosticsClientDispatcherV201(diagnosticsListener),
                DisplayMessageClientDispatcherV201(displayMessageListener),
                TariffAndCostClientDispatcherV201(tariffAndCostListener),
                DataTransferClientDispatcherV201(dataTransferListener),
                MeterValuesClientDispatcher(),
                SecurityClientDispatcher(),
            ),
            sendHook = { chargePointIdentity, message ->
                interceptor.intercept(chargePointIdentity, message)
            },
        )
    }

    @Single
    fun ocppClientV21(
        ocppClientEventsHandler: OcppClientEventsHandlerV21,
        interceptor: MessageInterceptor,
        ocppSessionRepository: OcppSessionRepository,
        availabilityListener: AvailabilityClientDispatcher.Listener,
        certificateManagementListener: CertificateManagementClientDispatcher.Listener,
        dataTransferListener: DataTransferClientDispatcher.Listener,
        diagnosticsListener: DiagnosticsClientDispatcher.Listener,
        displayMessageListener: DisplayMessageClientDispatcher.Listener,
        firmwareManagementListener: FirmwareManagementClientDispatcher.Listener,
        localAuthListListener: LocalAuthorizationListManagementClientDispatcher.Listener,
        provisioningListener: ProvisioningClientDispatcher.Listener,
        remoteControlListener: RemoteControlClientDispatcher.Listener,
        reservationListener: ReservationClientDispatcher.Listener,
        smartChargingListener: SmartChargingClientDispatcher.Listener,
        tariffAndCostListener: TariffAndCostClientDispatcher.Listener,
        transactionsListener: TransactionsClientDispatcher.Listener,
        tariffListener: TariffClientDispatcher.Listener,
        derListener: DerClientDispatcher.Listener,
        periodicEventStreamListener: PeriodicEventStreamClientDispatcher.Listener,
        priorityChargingListener: PriorityChargingClientDispatcher.Listener,
        dynamicScheduleListener: DynamicScheduleClientDispatcher.Listener,
        batterySwapListener: BatterySwapClientDispatcher.Listener,
        afrrListener: AfrrClientDispatcher.Listener,
    ): OcppClientV21 {
        val builder = OcppClientV21Builder()
            .onConnect { ocppSessionInfo, reconnecting ->
                ocppClientEventsHandler.onConnect(ocppSessionInfo, reconnecting)
            }
            .onDisconnect { ocppSessionInfo ->
                ocppClientEventsHandler.onDisconnect(ocppSessionInfo)
            }
            .localMode(ocppSessionRepository)
            .settings(
                OcppSettings(
                    nanoSecondDates = false,
                ),
            )
            .addAuthorization()
            .addAvailability(availabilityListener)
            .addCertificateManagement(certificateManagementListener)
            .addDataTransfer(dataTransferListener)
            .addDiagnostics(diagnosticsListener)
            .addDisplayMessage(displayMessageListener)
            .addFirmwareManagement(firmwareManagementListener)
            .addLocalAuthorizationListManagement(localAuthListListener)
            .addMeterValues()
            .addProvisioning(provisioningListener)
            .addRemoteControl(remoteControlListener)
            .addReservation(reservationListener)
            .addSecurity()
            .addSmartCharging(smartChargingListener)
            .addTariffAndCost(tariffAndCostListener)
            .addTransactions(transactionsListener)
            .addTariff(tariffListener)
            .addDer(derListener)
            .addPeriodicEventStream(periodicEventStreamListener)
            .addPriorityCharging(priorityChargingListener)
            .addDynamicSchedule(dynamicScheduleListener)
            .addBatterySwap(batterySwapListener)
            .addSettlement()
            .addAfrr(afrrListener)
        // addSendHook returns BaseOcppClientBuilder, which hides build() — call it last.
        builder.addSendHook { chargePointIdentity, message ->
            interceptor.intercept(chargePointIdentity, message)
        }
        return builder.build()
    }
}
