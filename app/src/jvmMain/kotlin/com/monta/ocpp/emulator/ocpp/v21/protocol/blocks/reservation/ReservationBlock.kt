// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.reservation

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CancelReservationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CancelReservationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CancelReservationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReservationStatusUpdateFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReservationStatusUpdateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReservationStatusUpdateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReserveNowFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReserveNowRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReserveNowResponse

val reservationFeatures = listOf(
    ReservationStatusUpdateFeature,
    CancelReservationFeature,
    ReserveNowFeature,
)

class ReservationClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = reservationFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is CancelReservationRequest -> listener.cancelReservation(ocppSessionInfo, request)
            is ReserveNowRequest -> listener.reserveNow(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun cancelReservation(
            ocppSessionInfo: OcppSession.Info,
            request: CancelReservationRequest,
        ): CancelReservationResponse

        suspend fun reserveNow(
            ocppSessionInfo: OcppSession.Info,
            request: ReserveNowRequest,
        ): ReserveNowResponse
    }

    interface Sender {
        suspend fun reservationStatusUpdate(
            request: ReservationStatusUpdateRequest,
        ): ReservationStatusUpdateResponse
    }
}
