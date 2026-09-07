package com.monta.ocpp.emulator.ocpp.v16.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.reservation.service.ChargePointReservationService
import com.monta.ocpp.emulator.ocpp.v16.reservation.CancelReservationConfirmation
import com.monta.ocpp.emulator.ocpp.v16.reservation.CancelReservationRequest
import com.monta.ocpp.emulator.ocpp.v16.reservation.ReservationClientProfile
import com.monta.ocpp.emulator.ocpp.v16.reservation.ReserveNowConfirmation
import com.monta.ocpp.emulator.ocpp.v16.reservation.ReserveNowRequest
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import javax.inject.Singleton

@Singleton
class ReservationHandler(
    private val chargePointService: ChargePointService,
    private val reservationService: ChargePointReservationService,
) : ReservationClientProfile.Listener {

    override suspend fun reserveNow(
        ocppSessionInfo: OcppSession.Info,
        request: ReserveNowRequest,
    ): ReserveNowConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val status = reservationService.reserveNow(
            chargePoint = chargePoint,
            connectorId = request.connectorId,
            reservationId = request.reservationId,
            idTag = request.idTag,
            parentIdTag = request.parentIdTag,
            expiryDate = request.expiryDate.toInstant(),
        )
        GlobalLogger.info(
            chargePoint,
            "ReserveNow reservationId=${request.reservationId} connectorId=${request.connectorId} → $status",
        )
        return ReserveNowConfirmation(status = status)
    }

    override suspend fun cancelReservation(
        ocppSessionInfo: OcppSession.Info,
        request: CancelReservationRequest,
    ): CancelReservationConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val status = reservationService.cancelReservation(
            chargePoint = chargePoint,
            reservationId = request.reservationId,
        )
        GlobalLogger.info(
            chargePoint,
            "CancelReservation reservationId=${request.reservationId} → $status",
        )
        return CancelReservationConfirmation(status = status)
    }
}
