package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.smartcharging.SmartChargingClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearChargingProfileRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearChargingProfileResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetChargingProfilesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetChargingProfilesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCompositeScheduleRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCompositeScheduleResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetChargingProfileRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetChargingProfileResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearChargingProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetChargingProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class SmartChargingHandler : SmartChargingClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun clearChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: ClearChargingProfileRequest,
    ): ClearChargingProfileResponse {
        stateStore.record("ClearChargingProfile", request)
        return ClearChargingProfileResponse(status = ClearChargingProfileStatusEnum.Accepted)
    }

    override suspend fun getChargingProfiles(
        ocppSessionInfo: OcppSession.Info,
        request: GetChargingProfilesRequest,
    ): GetChargingProfilesResponse {
        stateStore.record("GetChargingProfiles", request)
        return GetChargingProfilesResponse(status = GetChargingProfileStatusEnum.Accepted)
    }

    override suspend fun getCompositeSchedule(
        ocppSessionInfo: OcppSession.Info,
        request: GetCompositeScheduleRequest,
    ): GetCompositeScheduleResponse {
        stateStore.record("GetCompositeSchedule", request)
        return GetCompositeScheduleResponse(status = GenericStatusEnum.Accepted)
    }

    override suspend fun setChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: SetChargingProfileRequest,
    ): SetChargingProfileResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("SetChargingProfile", request)
        return SetChargingProfileResponse(status = ChargingProfileStatusEnum.Accepted)
    }
}
