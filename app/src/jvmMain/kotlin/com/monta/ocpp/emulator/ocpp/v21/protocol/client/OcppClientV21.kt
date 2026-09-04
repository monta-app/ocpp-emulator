// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.client

import com.monta.library.ocpp.client.OcppClient
import com.monta.library.ocpp.common.OcppClientConnectionEvent
import com.monta.library.ocpp.common.OcppClientDisconnectionEvent
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.serialization.SerializationMode
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.session.OcppSessionRepository
import com.monta.library.ocpp.common.transport.OcppSettings
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.authorization.AuthorizationClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.availability.AvailabilityClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.batteryswap.BatterySwapClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.certificatemanagement.CertificateManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.datatransfer.DataTransferClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.der.DerClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.diagnostics.DiagnosticsClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.displaymessage.DisplayMessageClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.dynamicschedule.DynamicScheduleClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.firmwaremanagement.FirmwareManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.metervalues.MeterValuesClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.periodiceventstream.PeriodicEventStreamClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.prioritycharging.PriorityChargingClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.provisioning.ProvisioningClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.reservation.ReservationClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.security.SecurityClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.settlement.SettlementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.smartcharging.SmartChargingClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariff.TariffClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.transactions.TransactionsClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.OcppErrorResponderV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AuthorizeRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AuthorizeResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BatterySwapRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BatterySwapResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BootNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BootNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearedChargingLimitRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearedChargingLimitResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DataTransferRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DataTransferResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.FirmwareStatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.FirmwareStatusNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.Get15118EVCertificateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.Get15118EVCertificateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateChainStatusRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateChainStatusResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateStatusRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateStatusResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.HeartbeatRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.HeartbeatResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.LogStatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.LogStatusNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.MeterValuesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.MeterValuesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyAllowedEnergyTransferRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyAllowedEnergyTransferResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyChargingLimitRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyChargingLimitResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyCustomerInformationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyCustomerInformationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERAlarmRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERAlarmResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERStartStopRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERStartStopResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDisplayMessagesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDisplayMessagesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingNeedsRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingNeedsResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingScheduleRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingScheduleResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEventRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEventResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyMonitoringReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyMonitoringReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPriorityChargingRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPriorityChargingResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifySettlementRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifySettlementResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyWebPaymentStartedRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyWebPaymentStartedResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareStatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareStatusNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PullDynamicScheduleUpdateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PullDynamicScheduleUpdateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportChargingProfilesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportChargingProfilesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportDERControlRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportDERControlResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReservationStatusUpdateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReservationStatusUpdateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SecurityEventNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SecurityEventNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SignCertificateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SignCertificateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.StatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.StatusNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TransactionEventRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TransactionEventResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.VatNumberValidationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.VatNumberValidationResponse

class OcppClientV21(
    onConnect: OcppClientConnectionEvent,
    onDisconnect: OcppClientDisconnectionEvent,
    ocppSessionRepository: OcppSessionRepository,
    settings: OcppSettings,
    profiles: Set<ProfileDispatcher>,
    sendHook: suspend (String, String) -> String?,
) : OcppClient(
    onConnect = onConnect,
    onDisconnect = onDisconnect,
    ocppSessionRepository = ocppSessionRepository,
    serializationMode = SerializationMode.OCPP_2,
    ocppErrorResponder = OcppErrorResponderV21,
    settings = settings,
    profiles = profiles,
    sendHook = sendHook,
) {
    fun asAuthorization(
        ocppSessionInfo: OcppSession.Info,
    ): AuthorizationClientDispatcher.Sender {
        return object : AuthorizationClientDispatcher.Sender {
            override suspend fun authorize(
                request: AuthorizeRequest,
            ) = send(ocppSessionInfo, request) as AuthorizeResponse
        }
    }

    fun asAvailability(
        ocppSessionInfo: OcppSession.Info,
    ): AvailabilityClientDispatcher.Sender {
        return object : AvailabilityClientDispatcher.Sender {
            override suspend fun heartbeat(
                request: HeartbeatRequest,
            ) = send(ocppSessionInfo, request) as HeartbeatResponse

            override suspend fun statusNotification(
                request: StatusNotificationRequest,
            ) = send(ocppSessionInfo, request) as StatusNotificationResponse
        }
    }

    fun asCertificateManagement(
        ocppSessionInfo: OcppSession.Info,
    ): CertificateManagementClientDispatcher.Sender {
        return object : CertificateManagementClientDispatcher.Sender {
            override suspend fun get15118EVCertificate(
                request: Get15118EVCertificateRequest,
            ) = send(ocppSessionInfo, request) as Get15118EVCertificateResponse

            override suspend fun getCertificateStatus(
                request: GetCertificateStatusRequest,
            ) = send(ocppSessionInfo, request) as GetCertificateStatusResponse

            override suspend fun getCertificateChainStatus(
                request: GetCertificateChainStatusRequest,
            ) = send(ocppSessionInfo, request) as GetCertificateChainStatusResponse

            override suspend fun signCertificate(
                request: SignCertificateRequest,
            ) = send(ocppSessionInfo, request) as SignCertificateResponse
        }
    }

    fun asDataTransfer(
        ocppSessionInfo: OcppSession.Info,
    ): DataTransferClientDispatcher.Sender {
        return object : DataTransferClientDispatcher.Sender {
            override suspend fun dataTransfer(
                request: DataTransferRequest,
            ) = send(ocppSessionInfo, request) as DataTransferResponse
        }
    }

    fun asDiagnostics(
        ocppSessionInfo: OcppSession.Info,
    ): DiagnosticsClientDispatcher.Sender {
        return object : DiagnosticsClientDispatcher.Sender {
            override suspend fun logStatusNotification(
                request: LogStatusNotificationRequest,
            ) = send(ocppSessionInfo, request) as LogStatusNotificationResponse

            override suspend fun notifyCustomerInformation(
                request: NotifyCustomerInformationRequest,
            ) = send(ocppSessionInfo, request) as NotifyCustomerInformationResponse

            override suspend fun notifyEvent(
                request: NotifyEventRequest,
            ) = send(ocppSessionInfo, request) as NotifyEventResponse

            override suspend fun notifyMonitoringReport(
                request: NotifyMonitoringReportRequest,
            ) = send(ocppSessionInfo, request) as NotifyMonitoringReportResponse
        }
    }

    fun asDisplayMessage(
        ocppSessionInfo: OcppSession.Info,
    ): DisplayMessageClientDispatcher.Sender {
        return object : DisplayMessageClientDispatcher.Sender {
            override suspend fun notifyDisplayMessages(
                request: NotifyDisplayMessagesRequest,
            ) = send(ocppSessionInfo, request) as NotifyDisplayMessagesResponse
        }
    }

    fun asFirmwareManagement(
        ocppSessionInfo: OcppSession.Info,
    ): FirmwareManagementClientDispatcher.Sender {
        return object : FirmwareManagementClientDispatcher.Sender {
            override suspend fun firmwareStatusNotification(
                request: FirmwareStatusNotificationRequest,
            ) = send(ocppSessionInfo, request) as FirmwareStatusNotificationResponse

            override suspend fun publishFirmwareStatusNotification(
                request: PublishFirmwareStatusNotificationRequest,
            ) = send(ocppSessionInfo, request) as PublishFirmwareStatusNotificationResponse
        }
    }

    fun asMeterValues(
        ocppSessionInfo: OcppSession.Info,
    ): MeterValuesClientDispatcher.Sender {
        return object : MeterValuesClientDispatcher.Sender {
            override suspend fun meterValues(
                request: MeterValuesRequest,
            ) = send(ocppSessionInfo, request) as MeterValuesResponse
        }
    }

    fun asProvisioning(
        ocppSessionInfo: OcppSession.Info,
    ): ProvisioningClientDispatcher.Sender {
        return object : ProvisioningClientDispatcher.Sender {
            override suspend fun bootNotification(
                request: BootNotificationRequest,
            ) = send(ocppSessionInfo, request) as BootNotificationResponse

            override suspend fun notifyReport(
                request: NotifyReportRequest,
            ) = send(ocppSessionInfo, request) as NotifyReportResponse
        }
    }

    fun asReservation(
        ocppSessionInfo: OcppSession.Info,
    ): ReservationClientDispatcher.Sender {
        return object : ReservationClientDispatcher.Sender {
            override suspend fun reservationStatusUpdate(
                request: ReservationStatusUpdateRequest,
            ) = send(ocppSessionInfo, request) as ReservationStatusUpdateResponse
        }
    }

    fun asSecurity(
        ocppSessionInfo: OcppSession.Info,
    ): SecurityClientDispatcher.Sender {
        return object : SecurityClientDispatcher.Sender {
            override suspend fun securityEventNotification(
                request: SecurityEventNotificationRequest,
            ) = send(ocppSessionInfo, request) as SecurityEventNotificationResponse
        }
    }

    fun asSmartCharging(
        ocppSessionInfo: OcppSession.Info,
    ): SmartChargingClientDispatcher.Sender {
        return object : SmartChargingClientDispatcher.Sender {
            override suspend fun clearedChargingLimit(
                request: ClearedChargingLimitRequest,
            ) = send(ocppSessionInfo, request) as ClearedChargingLimitResponse

            override suspend fun notifyChargingLimit(
                request: NotifyChargingLimitRequest,
            ) = send(ocppSessionInfo, request) as NotifyChargingLimitResponse

            override suspend fun notifyEVChargingNeeds(
                request: NotifyEVChargingNeedsRequest,
            ) = send(ocppSessionInfo, request) as NotifyEVChargingNeedsResponse

            override suspend fun notifyEVChargingSchedule(
                request: NotifyEVChargingScheduleRequest,
            ) = send(ocppSessionInfo, request) as NotifyEVChargingScheduleResponse

            override suspend fun reportChargingProfiles(
                request: ReportChargingProfilesRequest,
            ) = send(ocppSessionInfo, request) as ReportChargingProfilesResponse

            override suspend fun notifyAllowedEnergyTransfer(
                request: NotifyAllowedEnergyTransferRequest,
            ) = send(ocppSessionInfo, request) as NotifyAllowedEnergyTransferResponse
        }
    }

    fun asTransactions(
        ocppSessionInfo: OcppSession.Info,
    ): TransactionsClientDispatcher.Sender {
        return object : TransactionsClientDispatcher.Sender {
            override suspend fun transactionEvent(
                request: TransactionEventRequest,
            ) = send(ocppSessionInfo, request) as TransactionEventResponse
        }
    }

    fun asTariff(
        ocppSessionInfo: OcppSession.Info,
    ): TariffClientDispatcher.Sender {
        return object : TariffClientDispatcher.Sender {
            override suspend fun vatNumberValidation(
                request: VatNumberValidationRequest,
            ) = send(ocppSessionInfo, request) as VatNumberValidationResponse
        }
    }

    fun asDer(
        ocppSessionInfo: OcppSession.Info,
    ): DerClientDispatcher.Sender {
        return object : DerClientDispatcher.Sender {
            override suspend fun notifyDERAlarm(
                request: NotifyDERAlarmRequest,
            ) = send(ocppSessionInfo, request) as NotifyDERAlarmResponse

            override suspend fun notifyDERStartStop(
                request: NotifyDERStartStopRequest,
            ) = send(ocppSessionInfo, request) as NotifyDERStartStopResponse

            override suspend fun reportDERControl(
                request: ReportDERControlRequest,
            ) = send(ocppSessionInfo, request) as ReportDERControlResponse
        }
    }

    fun asPeriodicEventStream(
        ocppSessionInfo: OcppSession.Info,
    ): PeriodicEventStreamClientDispatcher.Sender {
        return object : PeriodicEventStreamClientDispatcher.Sender {
            override suspend fun notifyPeriodicEventStream(
                request: NotifyPeriodicEventStreamRequest,
            ) = send(ocppSessionInfo, request) as NotifyPeriodicEventStreamResponse
        }
    }

    fun asPriorityCharging(
        ocppSessionInfo: OcppSession.Info,
    ): PriorityChargingClientDispatcher.Sender {
        return object : PriorityChargingClientDispatcher.Sender {
            override suspend fun notifyPriorityCharging(
                request: NotifyPriorityChargingRequest,
            ) = send(ocppSessionInfo, request) as NotifyPriorityChargingResponse
        }
    }

    fun asDynamicSchedule(
        ocppSessionInfo: OcppSession.Info,
    ): DynamicScheduleClientDispatcher.Sender {
        return object : DynamicScheduleClientDispatcher.Sender {
            override suspend fun pullDynamicScheduleUpdate(
                request: PullDynamicScheduleUpdateRequest,
            ) = send(ocppSessionInfo, request) as PullDynamicScheduleUpdateResponse
        }
    }

    fun asBatterySwap(
        ocppSessionInfo: OcppSession.Info,
    ): BatterySwapClientDispatcher.Sender {
        return object : BatterySwapClientDispatcher.Sender {
            override suspend fun batterySwap(
                request: BatterySwapRequest,
            ) = send(ocppSessionInfo, request) as BatterySwapResponse
        }
    }

    fun asSettlement(
        ocppSessionInfo: OcppSession.Info,
    ): SettlementClientDispatcher.Sender {
        return object : SettlementClientDispatcher.Sender {
            override suspend fun notifySettlement(
                request: NotifySettlementRequest,
            ) = send(ocppSessionInfo, request) as NotifySettlementResponse

            override suspend fun notifyWebPaymentStarted(
                request: NotifyWebPaymentStartedRequest,
            ) = send(ocppSessionInfo, request) as NotifyWebPaymentStartedResponse
        }
    }
}
