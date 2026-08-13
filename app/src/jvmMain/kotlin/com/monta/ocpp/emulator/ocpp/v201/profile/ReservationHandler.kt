package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.reservation.CancelReservationRequest
import com.monta.library.ocpp.v201.blocks.reservation.CancelReservationResponse
import com.monta.library.ocpp.v201.blocks.reservation.ReservationClientDispatcher
import com.monta.library.ocpp.v201.blocks.reservation.ReserveNowRequest
import com.monta.library.ocpp.v201.blocks.reservation.ReserveNowResponse
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.reservation.service.ChargePointReservationService
import javax.inject.Singleton

@Singleton
class ReservationHandler(
    private val chargePointService: ChargePointService,
    private val reservationService: ChargePointReservationService,
) : ReservationClientDispatcher.Listener {
    override suspend fun cancelReservation(
        ocppSessionInfo: OcppSession.Info,
        request: CancelReservationRequest,
    ): CancelReservationResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val status = reservationService.cancelReservation(chargePoint, request.reservationId.toInt())
        return CancelReservationResponse(status = CancelReservationResponse.Status.valueOf(status.name))
    }

    override suspend fun reserveNow(
        ocppSessionInfo: OcppSession.Info,
        request: ReserveNowRequest,
    ): ReserveNowResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val status = reservationService.reserveNow(
            chargePoint = chargePoint,
            connectorId = (request.evseId ?: 0L).toInt(),
            reservationId = request.id.toInt(),
            idTag = request.idToken.idToken,
            parentIdTag = request.groupIdToken?.idToken,
            expiryDate = request.expiryDateTime.toInstant(),
        )
        return ReserveNowResponse(status = ReserveNowResponse.Status.valueOf(status.name))
    }
}
