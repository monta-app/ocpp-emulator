package com.monta.ocpp.emulator.ocpp.v16.reservation

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import java.time.ZonedDateTime

object ReserveNowFeature : Feature {
    override val name: String = "ReserveNow"
    override val requestType: Class<out OcppRequest> = ReserveNowRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ReserveNowConfirmation::class.java
}

enum class ReservationStatus {
    Accepted,
    Faulted,
    Occupied,
    Rejected,
    Unavailable,
}

data class ReserveNowRequest(
    val connectorId: Int,
    val expiryDate: ZonedDateTime,
    val idTag: String,
    val reservationId: Int,
    val parentIdTag: String? = null,
) : OcppRequest

data class ReserveNowConfirmation(
    val status: ReservationStatus,
) : OcppConfirmation

object CancelReservationFeature : Feature {
    override val name: String = "CancelReservation"
    override val requestType: Class<out OcppRequest> = CancelReservationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = CancelReservationConfirmation::class.java
}

enum class CancelReservationStatus {
    Accepted,
    Rejected,
}

data class CancelReservationRequest(
    val reservationId: Int,
) : OcppRequest

data class CancelReservationConfirmation(
    val status: CancelReservationStatus,
) : OcppConfirmation
