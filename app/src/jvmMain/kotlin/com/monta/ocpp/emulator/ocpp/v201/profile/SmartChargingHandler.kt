package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.smartcharging.ClearChargingProfileRequest
import com.monta.library.ocpp.v201.blocks.smartcharging.ClearChargingProfileResponse
import com.monta.library.ocpp.v201.blocks.smartcharging.GetChargingProfilesRequest
import com.monta.library.ocpp.v201.blocks.smartcharging.GetChargingProfilesResponse
import com.monta.library.ocpp.v201.blocks.smartcharging.GetCompositeScheduleRequest
import com.monta.library.ocpp.v201.blocks.smartcharging.GetCompositeScheduleResponse
import com.monta.library.ocpp.v201.blocks.smartcharging.SetChargingProfileRequest
import com.monta.library.ocpp.v201.blocks.smartcharging.SetChargingProfileResponse
import com.monta.library.ocpp.v201.blocks.smartcharging.SmartChargingClientDispatcher
import com.monta.library.ocpp.v201.common.GenericStatus
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import javax.inject.Singleton

@Singleton
class SmartChargingHandler(
    private val chargePointService: ChargePointService,
) : SmartChargingClientDispatcher.Listener {
    override suspend fun clearChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: ClearChargingProfileRequest,
    ): ClearChargingProfileResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return ClearChargingProfileResponse(status = ClearChargingProfileResponse.Status.Accepted)
    }

    override suspend fun getChargingProfiles(
        ocppSessionInfo: OcppSession.Info,
        request: GetChargingProfilesRequest,
    ): GetChargingProfilesResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return GetChargingProfilesResponse(status = GetChargingProfilesResponse.Status.Accepted)
    }

    override suspend fun getCompositeSchedule(
        ocppSessionInfo: OcppSession.Info,
        request: GetCompositeScheduleRequest,
    ): GetCompositeScheduleResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return GetCompositeScheduleResponse(status = GenericStatus.Accepted)
    }

    override suspend fun setChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: SetChargingProfileRequest,
    ): SetChargingProfileResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return SetChargingProfileResponse(status = SetChargingProfileResponse.Status.Accepted)
    }
}
