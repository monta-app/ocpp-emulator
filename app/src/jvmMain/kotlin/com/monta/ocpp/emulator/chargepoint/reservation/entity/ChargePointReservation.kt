package com.monta.ocpp.emulator.chargepoint.reservation.entity

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.timestamp
import java.time.Instant

object ChargePointReservationTable : LongIdTable("charge_point_reservation") {
    val chargePointId = reference("charge_point_id", ChargePointTable)
    val connectorId = reference("connector_id", ChargePointConnectorTable)
    val reservationId = integer("reservation_id")
    val idTag = varchar("id_tag", 20)
    val parentIdTag = varchar("parent_id_tag", 20).nullable()
    val expiryDate = timestamp("expiry_date")
    val connectorPosition = integer("connector_position")
    val createdAt = timestamp("created_at")
    val active = bool("active").default(true)
}

class ChargePointReservationDAO(
    id: EntityID<Long>,
) : LongEntity(id) {
    companion object : LongEntityClass<ChargePointReservationDAO>(ChargePointReservationTable) {
        fun newInstance(
            chargePoint: ChargePointDAO,
            connector: ChargePointConnectorDAO,
            reservationId: Int,
            idTag: String,
            parentIdTag: String?,
            expiryDate: Instant,
        ): ChargePointReservationDAO {
            return ChargePointReservationDAO.new {
                this.chargePoint = chargePoint
                this.connector = connector
                this.reservationId = reservationId
                this.idTag = idTag
                this.parentIdTag = parentIdTag
                this.expiryDate = expiryDate
                this.connectorPosition = connector.position
                this.createdAt = Instant.now()
                this.active = true
            }
        }
    }

    var chargePoint by ChargePointDAO referencedOn ChargePointReservationTable.chargePointId
    var connector by ChargePointConnectorDAO referencedOn ChargePointReservationTable.connectorId
    var reservationId by ChargePointReservationTable.reservationId
    var idTag by ChargePointReservationTable.idTag
    var parentIdTag by ChargePointReservationTable.parentIdTag
    var expiryDate by ChargePointReservationTable.expiryDate
    var connectorPosition by ChargePointReservationTable.connectorPosition
    var createdAt by ChargePointReservationTable.createdAt
    var active by ChargePointReservationTable.active
}
