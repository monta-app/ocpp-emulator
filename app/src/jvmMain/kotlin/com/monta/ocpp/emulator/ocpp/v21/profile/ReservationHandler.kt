package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.reservation.service.ChargePointReservationService
import com.monta.ocpp.emulator.ocpp.v16.reservation.CancelReservationStatus
import com.monta.ocpp.emulator.ocpp.v16.reservation.ReservationStatus
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.reservation.ReservationClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CancelReservationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CancelReservationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReserveNowRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReserveNowResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CancelReservationStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ReserveNowStatusEnum
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class ReservationHandler : ReservationClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val reservationService: ChargePointReservationService by injectAnywhere()

    override suspend fun reserveNow(
        ocppSessionInfo: OcppSession.Info,
        request: ReserveNowRequest,
    ): ReserveNowResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val status = reservationService.reserveNow(
            chargePoint = chargePoint,
            connectorId = request.evseId ?: 0,
            reservationId = request.id,
            idTag = request.idToken.idToken,
            parentIdTag = request.groupIdToken?.idToken,
            expiryDate = request.expiryDateTime.toInstant(),
        )
        GlobalLogger.info(chargePoint, "ReserveNow reservationId=${request.id} evseId=${request.evseId} → $status")
        return ReserveNowResponse(status = status.toOcpp21())
    }

    override suspend fun cancelReservation(
        ocppSessionInfo: OcppSession.Info,
        request: CancelReservationRequest,
    ): CancelReservationResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val status = reservationService.cancelReservation(
            chargePoint = chargePoint,
            reservationId = request.reservationId,
        )
        GlobalLogger.info(chargePoint, "CancelReservation reservationId=${request.reservationId} → $status")
        return CancelReservationResponse(
            status = when (status) {
                CancelReservationStatus.Accepted -> CancelReservationStatusEnum.Accepted
                CancelReservationStatus.Rejected -> CancelReservationStatusEnum.Rejected
            },
        )
    }

    private fun ReservationStatus.toOcpp21(): ReserveNowStatusEnum = when (this) {
        ReservationStatus.Accepted -> ReserveNowStatusEnum.Accepted
        ReservationStatus.Faulted -> ReserveNowStatusEnum.Faulted
        ReservationStatus.Occupied -> ReserveNowStatusEnum.Occupied
        ReservationStatus.Rejected -> ReserveNowStatusEnum.Rejected
        ReservationStatus.Unavailable -> ReserveNowStatusEnum.Unavailable
    }
}
