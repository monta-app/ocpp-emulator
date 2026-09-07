// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.provisioning

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BootNotificationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BootNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BootNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetBaseReportFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetBaseReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetBaseReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetReportFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetVariablesFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetVariablesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetVariablesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyReportFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ResetFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ResetRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ResetResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetNetworkProfileFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetNetworkProfileRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetNetworkProfileResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariablesFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariablesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariablesResponse

val provisioningFeatures = listOf(
    BootNotificationFeature,
    NotifyReportFeature,
    GetBaseReportFeature,
    GetReportFeature,
    GetVariablesFeature,
    ResetFeature,
    SetNetworkProfileFeature,
    SetVariablesFeature,
)

class ProvisioningClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = provisioningFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is GetBaseReportRequest -> listener.getBaseReport(ocppSessionInfo, request)
            is GetReportRequest -> listener.getReport(ocppSessionInfo, request)
            is GetVariablesRequest -> listener.getVariables(ocppSessionInfo, request)
            is ResetRequest -> listener.reset(ocppSessionInfo, request)
            is SetNetworkProfileRequest -> listener.setNetworkProfile(ocppSessionInfo, request)
            is SetVariablesRequest -> listener.setVariables(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun getBaseReport(
            ocppSessionInfo: OcppSession.Info,
            request: GetBaseReportRequest,
        ): GetBaseReportResponse

        suspend fun getReport(
            ocppSessionInfo: OcppSession.Info,
            request: GetReportRequest,
        ): GetReportResponse

        suspend fun getVariables(
            ocppSessionInfo: OcppSession.Info,
            request: GetVariablesRequest,
        ): GetVariablesResponse

        suspend fun reset(
            ocppSessionInfo: OcppSession.Info,
            request: ResetRequest,
        ): ResetResponse

        suspend fun setNetworkProfile(
            ocppSessionInfo: OcppSession.Info,
            request: SetNetworkProfileRequest,
        ): SetNetworkProfileResponse

        suspend fun setVariables(
            ocppSessionInfo: OcppSession.Info,
            request: SetVariablesRequest,
        ): SetVariablesResponse
    }

    interface Sender {
        suspend fun bootNotification(
            request: BootNotificationRequest,
        ): BootNotificationResponse

        suspend fun notifyReport(
            request: NotifyReportRequest,
        ): NotifyReportResponse
    }
}
