// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.smartcharging

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearChargingProfileFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearChargingProfileRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearChargingProfileResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearedChargingLimitFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearedChargingLimitRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearedChargingLimitResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetChargingProfilesFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetChargingProfilesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetChargingProfilesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCompositeScheduleFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCompositeScheduleRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCompositeScheduleResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyAllowedEnergyTransferFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyAllowedEnergyTransferRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyAllowedEnergyTransferResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyChargingLimitFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyChargingLimitRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyChargingLimitResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingNeedsFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingNeedsRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingNeedsResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingScheduleFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingScheduleRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyEVChargingScheduleResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportChargingProfilesFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportChargingProfilesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportChargingProfilesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetChargingProfileFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetChargingProfileRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetChargingProfileResponse

val smartchargingFeatures = listOf(
    ClearedChargingLimitFeature,
    NotifyChargingLimitFeature,
    NotifyEVChargingNeedsFeature,
    NotifyEVChargingScheduleFeature,
    ReportChargingProfilesFeature,
    NotifyAllowedEnergyTransferFeature,
    ClearChargingProfileFeature,
    GetChargingProfilesFeature,
    GetCompositeScheduleFeature,
    SetChargingProfileFeature,
)

class SmartChargingClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = smartchargingFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is ClearChargingProfileRequest -> listener.clearChargingProfile(ocppSessionInfo, request)
            is GetChargingProfilesRequest -> listener.getChargingProfiles(ocppSessionInfo, request)
            is GetCompositeScheduleRequest -> listener.getCompositeSchedule(ocppSessionInfo, request)
            is SetChargingProfileRequest -> listener.setChargingProfile(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun clearChargingProfile(
            ocppSessionInfo: OcppSession.Info,
            request: ClearChargingProfileRequest,
        ): ClearChargingProfileResponse

        suspend fun getChargingProfiles(
            ocppSessionInfo: OcppSession.Info,
            request: GetChargingProfilesRequest,
        ): GetChargingProfilesResponse

        suspend fun getCompositeSchedule(
            ocppSessionInfo: OcppSession.Info,
            request: GetCompositeScheduleRequest,
        ): GetCompositeScheduleResponse

        suspend fun setChargingProfile(
            ocppSessionInfo: OcppSession.Info,
            request: SetChargingProfileRequest,
        ): SetChargingProfileResponse
    }

    interface Sender {
        suspend fun clearedChargingLimit(
            request: ClearedChargingLimitRequest,
        ): ClearedChargingLimitResponse

        suspend fun notifyChargingLimit(
            request: NotifyChargingLimitRequest,
        ): NotifyChargingLimitResponse

        suspend fun notifyEVChargingNeeds(
            request: NotifyEVChargingNeedsRequest,
        ): NotifyEVChargingNeedsResponse

        suspend fun notifyEVChargingSchedule(
            request: NotifyEVChargingScheduleRequest,
        ): NotifyEVChargingScheduleResponse

        suspend fun reportChargingProfiles(
            request: ReportChargingProfilesRequest,
        ): ReportChargingProfilesResponse

        suspend fun notifyAllowedEnergyTransfer(
            request: NotifyAllowedEnergyTransferRequest,
        ): NotifyAllowedEnergyTransferResponse
    }
}
