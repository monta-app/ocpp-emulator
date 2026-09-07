package com.monta.ocpp.emulator.ocpp.v16.reservation

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.library.ocpp.v16.error.MessageErrorCodeV16

class ReservationClientProfile(
    private val listener: Listener,
) : ProfileDispatcher {

    override val featureList: List<Feature> = listOf(
        ReserveNowFeature,
        CancelReservationFeature,
    )

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is ReserveNowRequest -> listener.reserveNow(ocppSessionInfo, request)
            is CancelReservationRequest -> listener.cancelReservation(ocppSessionInfo, request)
            else -> throw OcppCallException(
                MessageErrorCodeV16.NotSupported,
                "Requested Action [${request.actionName()}] is recognized but not supported by the receiver",
            )
        }
    }

    interface Listener {
        suspend fun reserveNow(
            ocppSessionInfo: OcppSession.Info,
            request: ReserveNowRequest,
        ): ReserveNowConfirmation

        suspend fun cancelReservation(
            ocppSessionInfo: OcppSession.Info,
            request: CancelReservationRequest,
        ): CancelReservationConfirmation
    }
}
