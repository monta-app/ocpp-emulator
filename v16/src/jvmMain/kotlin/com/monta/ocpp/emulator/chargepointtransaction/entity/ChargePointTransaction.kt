package com.monta.ocpp.emulator.chargepointtransaction.entity

import com.monta.library.ocpp.v16.core.Reason
import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.common.util.ChargingProfileCalculator
import com.monta.ocpp.emulator.common.util.MontaSerialization
import com.monta.ocpp.emulator.common.util.json
import com.monta.ocpp.emulator.logger.Loggable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

// Table Definition
object ChargePointTransaction : LongIdTable("charge_point_transaction") {
    val chargePointId = reference("charge_point_id", ChargePointTable)
    val connectorId = reference("connector_id", ChargePointConnectorTable)

    var connectorPosition = integer("connector_position")
    var externalId = integer("external_id")
    var idTag = varchar("id_tag", 128)
    var statusAt = timestamp("status_at")
    var startMeter = double("start_meter")
    var startTime = timestamp("start_time")
    var meterValuesAt = timestamp("meter_values_at")
    var endMeter = double("end_meter")
    var endMeterAt = timestamp("end_meter_at")
    var endTime = timestamp("end_time").nullable()
    var endReasonDescription = varchar("end_reason_description", 512).nullable()
    var endReason = enumerationByName("end_reason", 64, Reason::class).nullable()
    var chargingProfile = json<ChargingProfile>(
        name = "charging_profile",
        objectMapper = MontaSerialization.getDefaultMapper()
    ).nullable()
    var createdAt = timestamp("created_at").default(Instant.now())
}

// DAO
class ChargePointTransactionDAO(
    id: EntityID<Long>
) : LongEntity(id), Loggable {
    companion object : LongEntityClass<ChargePointTransactionDAO>(ChargePointTransaction) {
        fun newInstance(
            chargePoint: ChargePointDAO,
            chargePointConnector: ChargePointConnectorDAO,
            externalId: Int,
            idTag: String,
            statusAt: Instant = Instant.now(),
            startMeter: Double = 0.0,
            startTime: Instant = Instant.now(),
            meterValuesAt: Instant = Instant.now(),
            endMeter: Double = startMeter,
            endMeterAt: Instant = Instant.now(),
            endTime: Instant? = null,
            endReason: Reason? = null,
            endReasonDescription: String? = null
        ): ChargePointTransactionDAO {
            return ChargePointTransactionDAO.new {
                this.chargePoint = chargePoint
                this.chargePointConnector = chargePointConnector
                this.connectorPosition = chargePointConnector.position
                this.externalId = externalId
                this.idTag = idTag
                this.statusAt = statusAt
                this.startMeter = startMeter
                this.startTime = startTime
                this.meterValuesAt = meterValuesAt
                this.endMeter = endMeter
                this.endMeterAt = endMeterAt
                this.endTime = endTime
                this.endReason = endReason
                this.endReasonDescription = endReasonDescription
                this.chargingProfile = null
            }
        }
    }

    var chargePointId by ChargePointTransaction.chargePointId
    var chargePoint by ChargePointDAO referencedOn ChargePointTransaction.chargePointId
    var chargePointConnector by ChargePointConnectorDAO referencedOn ChargePointTransaction.connectorId

    var connectorPosition by ChargePointTransaction.connectorPosition
    var externalId by ChargePointTransaction.externalId
    var idTag by ChargePointTransaction.idTag
    var statusAt by ChargePointTransaction.statusAt
    var startMeter by ChargePointTransaction.startMeter
    var startTime by ChargePointTransaction.startTime
    var meterValuesAt by ChargePointTransaction.meterValuesAt
    var endMeter by ChargePointTransaction.endMeter
    var endMeterAt by ChargePointTransaction.endMeterAt
    var endTime by ChargePointTransaction.endTime
    var endReason by ChargePointTransaction.endReason
    var endReasonDescription by ChargePointTransaction.endReasonDescription
    var chargingProfile by ChargePointTransaction.chargingProfile
    var createdAt by ChargePointTransaction.createdAt

    fun isOwner(connector: ChargePointConnectorDAO): Boolean {
        return this.chargePointId == connector.chargePointId
    }

    fun isOwner(chargePoint: ChargePointDAO): Boolean {
        return this.chargePointId == chargePoint.id
    }

    fun canStop(): Boolean {
        return endTime == null
    }

    override fun chargePointId(): Long {
        return chargePointId.value
    }

    override fun connectorPosition(): Int {
        return connectorPosition
    }

    fun getChargingProfileWatts(): Double? {
        return ChargingProfileCalculator.getWatts(this)
    }

    fun clearChargingProfile() {
        transaction {
            chargingProfile = null
        }
    }
}
