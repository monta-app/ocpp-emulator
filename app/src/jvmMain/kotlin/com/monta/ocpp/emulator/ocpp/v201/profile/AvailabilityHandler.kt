package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.availability.AvailabilityClientDispatcher
import com.monta.library.ocpp.v201.blocks.availability.ChangeAvailabilityRequest
import com.monta.library.ocpp.v201.blocks.availability.ChangeAvailabilityResponse
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import javax.inject.Singleton

@Singleton
class AvailabilityHandler(
    private val chargePointService: ChargePointService,
) : AvailabilityClientDispatcher.Listener {
    override suspend fun changeAvailability(
        ocppSessionInfo: OcppSession.Info,
        request: ChangeAvailabilityRequest,
    ): ChangeAvailabilityResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return ChangeAvailabilityResponse(status = ChangeAvailabilityResponse.Status.Accepted)
    }
}
