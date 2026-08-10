package com.monta.ocpp.emulator.chargepoint.reservation.service

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.model.OcppVersion
import com.monta.ocpp.emulator.chargepoint.reservation.entity.ChargePointReservationDAO
import com.monta.ocpp.emulator.chargepoint.reservation.repository.ChargePointReservationRepository
import com.monta.ocpp.emulator.ocpp.v16.extension.setStatus
import com.monta.ocpp.emulator.ocpp.v16.reservation.CancelReservationStatus
import com.monta.ocpp.emulator.ocpp.v16.reservation.ReservationStatus
import com.monta.ocpp.emulator.ocpp.v201.extension.setStatus201
import com.monta.ocpp.emulator.ocpp.v21.extension.setStatus21
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import javax.inject.Singleton

@Singleton
class ChargePointReservationService(
    private val reservationRepository: ChargePointReservationRepository,
) {

    suspend fun reserveNow(
        chargePoint: ChargePointDAO,
        connectorId: Int,
        reservationId: Int,
        idTag: String,
        parentIdTag: String?,
        expiryDate: Instant,
    ): ReservationStatus {
        if (expiryDate.isBefore(Instant.now())) {
            return ReservationStatus.Rejected
        }

        if (!chargePoint.canPerformAction) {
            return ReservationStatus.Unavailable
        }

        val connector = resolveConnector(chargePoint, connectorId)
            ?: return ReservationStatus.Rejected

        if (connector.errorCode != ChargePointErrorCode.NoError &&
            connector.status == ChargePointStatus.Faulted
        ) {
            return ReservationStatus.Faulted
        }

        if (connector.status == ChargePointStatus.Unavailable) {
            return ReservationStatus.Unavailable
        }

        if (connector.status == ChargePointStatus.Charging ||
            connector.status == ChargePointStatus.Preparing ||
            connector.status == ChargePointStatus.SuspendedEV ||
            connector.status == ChargePointStatus.SuspendedEVSE ||
            connector.hasActiveTransaction
        ) {
            return ReservationStatus.Occupied
        }

        val existingOnConnector = reservationRepository.findActiveByConnector(connector)
        if (existingOnConnector != null && existingOnConnector.reservationId != reservationId) {
            return ReservationStatus.Occupied
        }

        reservationRepository.create(
            chargePoint = chargePoint,
            connector = connector,
            reservationId = reservationId,
            idTag = idTag,
            parentIdTag = parentIdTag,
            expiryDate = expiryDate,
        )

        notifyConnectorStatus(
            connector = connector,
            status = ChargePointStatus.Reserved,
        )

        return ReservationStatus.Accepted
    }

    suspend fun cancelReservation(
        chargePoint: ChargePointDAO,
        reservationId: Int,
    ): CancelReservationStatus {
        val reservation = reservationRepository.findActiveByReservationId(chargePoint, reservationId)
            ?: return CancelReservationStatus.Rejected

        clearReservation(reservation, restoreAvailable = true)
        return CancelReservationStatus.Accepted
    }

    suspend fun expireDueReservations() {
        for (reservation in reservationRepository.findExpired()) {
            clearReservation(reservation, restoreAvailable = true)
        }
    }

    suspend fun clearReservation(
        reservation: ChargePointReservationDAO,
        restoreAvailable: Boolean,
    ) {
        val connector = transaction { reservation.connector }
        reservationRepository.deactivate(reservation)
        if (restoreAvailable && connector.status == ChargePointStatus.Reserved) {
            notifyConnectorStatus(
                connector = connector,
                status = ChargePointStatus.Available,
            )
        }
    }

    fun findActiveForConnector(
        connector: ChargePointConnectorDAO,
    ): ChargePointReservationDAO? = reservationRepository.findActiveByConnector(connector)

    private suspend fun notifyConnectorStatus(
        connector: ChargePointConnectorDAO,
        status: ChargePointStatus,
    ) {
        when (transaction { connector.getChargePoint().ocppVersion }) {
            OcppVersion.V16 -> connector.setStatus(
                status = status,
                errorCode = ChargePointErrorCode.NoError,
                forceUpdate = true,
            )
            OcppVersion.V201 -> connector.setStatus201(
                status = status,
                errorCode = ChargePointErrorCode.NoError,
                forceUpdate = true,
            )
            OcppVersion.V21 -> connector.setStatus21(
                status = status,
                errorCode = ChargePointErrorCode.NoError,
                forceUpdate = true,
            )
        }
    }

    private fun resolveConnector(
        chargePoint: ChargePointDAO,
        connectorId: Int,
    ): ChargePointConnectorDAO? {
        if (connectorId == 0) {
            return chargePoint.getConnectors().firstOrNull { connector ->
                connector.status == ChargePointStatus.Available &&
                    !connector.hasActiveTransaction
            }
        }
        return chargePoint.getConnector(connectorId)
    }
}
