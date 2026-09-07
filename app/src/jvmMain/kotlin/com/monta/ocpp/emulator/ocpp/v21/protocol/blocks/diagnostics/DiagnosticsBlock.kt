// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.diagnostics

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearVariableMonitoringFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearVariableMonitoringRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearVariableMonitoringResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CustomerInformationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CustomerInformationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CustomerInformationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLogFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLogRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLogResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetMonitoringReportFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetMonitoringReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetMonitoringReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.LogStatusNotificationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.LogStatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.LogStatusNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyCustomerInformationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyCustomerInformationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyCustomerInformationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEventFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEventRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEventResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyMonitoringReportFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyMonitoringReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyMonitoringReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringBaseFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringBaseRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringBaseResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringLevelFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringLevelRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringLevelResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariableMonitoringFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariableMonitoringRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariableMonitoringResponse

val diagnosticsFeatures = listOf(
    LogStatusNotificationFeature,
    NotifyCustomerInformationFeature,
    NotifyEventFeature,
    NotifyMonitoringReportFeature,
    ClearVariableMonitoringFeature,
    CustomerInformationFeature,
    GetLogFeature,
    GetMonitoringReportFeature,
    SetMonitoringBaseFeature,
    SetMonitoringLevelFeature,
    SetVariableMonitoringFeature,
)

class DiagnosticsClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = diagnosticsFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is ClearVariableMonitoringRequest -> listener.clearVariableMonitoring(ocppSessionInfo, request)
            is CustomerInformationRequest -> listener.customerInformation(ocppSessionInfo, request)
            is GetLogRequest -> listener.getLog(ocppSessionInfo, request)
            is GetMonitoringReportRequest -> listener.getMonitoringReport(ocppSessionInfo, request)
            is SetMonitoringBaseRequest -> listener.setMonitoringBase(ocppSessionInfo, request)
            is SetMonitoringLevelRequest -> listener.setMonitoringLevel(ocppSessionInfo, request)
            is SetVariableMonitoringRequest -> listener.setVariableMonitoring(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun clearVariableMonitoring(
            ocppSessionInfo: OcppSession.Info,
            request: ClearVariableMonitoringRequest,
        ): ClearVariableMonitoringResponse

        suspend fun customerInformation(
            ocppSessionInfo: OcppSession.Info,
            request: CustomerInformationRequest,
        ): CustomerInformationResponse

        suspend fun getLog(
            ocppSessionInfo: OcppSession.Info,
            request: GetLogRequest,
        ): GetLogResponse

        suspend fun getMonitoringReport(
            ocppSessionInfo: OcppSession.Info,
            request: GetMonitoringReportRequest,
        ): GetMonitoringReportResponse

        suspend fun setMonitoringBase(
            ocppSessionInfo: OcppSession.Info,
            request: SetMonitoringBaseRequest,
        ): SetMonitoringBaseResponse

        suspend fun setMonitoringLevel(
            ocppSessionInfo: OcppSession.Info,
            request: SetMonitoringLevelRequest,
        ): SetMonitoringLevelResponse

        suspend fun setVariableMonitoring(
            ocppSessionInfo: OcppSession.Info,
            request: SetVariableMonitoringRequest,
        ): SetVariableMonitoringResponse
    }

    interface Sender {
        suspend fun logStatusNotification(
            request: LogStatusNotificationRequest,
        ): LogStatusNotificationResponse

        suspend fun notifyCustomerInformation(
            request: NotifyCustomerInformationRequest,
        ): NotifyCustomerInformationResponse

        suspend fun notifyEvent(
            request: NotifyEventRequest,
        ): NotifyEventResponse

        suspend fun notifyMonitoringReport(
            request: NotifyMonitoringReportRequest,
        ): NotifyMonitoringReportResponse
    }
}
