package com.monta.ocpp.emulator.chargepoint.reservation.repository

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.reservation.entity.ChargePointReservationDAO
import com.monta.ocpp.emulator.chargepoint.reservation.entity.ChargePointReservationTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import javax.inject.Singleton

@Singleton
class ChargePointReservationRepository {

    fun findActiveByReservationId(
        chargePoint: ChargePointDAO,
        reservationId: Int,
    ): ChargePointReservationDAO? = transaction {
        ChargePointReservationDAO.find {
            (ChargePointReservationTable.chargePointId eq chargePoint.id) and
                (ChargePointReservationTable.reservationId eq reservationId) and
                (ChargePointReservationTable.active eq true)
        }.firstOrNull()
    }

    fun findActiveByConnector(
        connector: ChargePointConnectorDAO,
    ): ChargePointReservationDAO? = transaction {
        ChargePointReservationDAO.find {
            (ChargePointReservationTable.connectorId eq connector.id) and
                (ChargePointReservationTable.active eq true)
        }.firstOrNull()
    }

    fun findExpired(
        now: Instant = Instant.now(),
    ): List<ChargePointReservationDAO> = transaction {
        ChargePointReservationDAO.find {
            (ChargePointReservationTable.active eq true)
        }.filter { it.expiryDate.isBefore(now) }.toList()
    }

    fun create(
        chargePoint: ChargePointDAO,
        connector: ChargePointConnectorDAO,
        reservationId: Int,
        idTag: String,
        parentIdTag: String?,
        expiryDate: Instant,
    ): ChargePointReservationDAO = transaction {
        findActiveByReservationId(chargePoint, reservationId)?.let { existing ->
            existing.active = false
        }
        ChargePointReservationDAO.newInstance(
            chargePoint = chargePoint,
            connector = connector,
            reservationId = reservationId,
            idTag = idTag,
            parentIdTag = parentIdTag,
            expiryDate = expiryDate,
        )
    }

    fun deactivate(
        reservation: ChargePointReservationDAO,
    ) = transaction {
        reservation.active = false
    }
}
