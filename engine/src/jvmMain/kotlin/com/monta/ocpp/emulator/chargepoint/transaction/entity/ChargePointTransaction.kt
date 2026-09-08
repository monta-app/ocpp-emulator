package com.monta.ocpp.emulator.chargepoint.transaction.entity

import com.monta.library.ocpp.v16.core.Reason
import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointTable
import com.monta.ocpp.emulator.ocpp.v16.smartcharging.ChargingProfileCalculator
import com.monta.ocpp.emulator.platform.logging.model.Loggable
import com.monta.ocpp.emulator.platform.util.MontaSerialization
import com.monta.ocpp.emulator.platform.util.json
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant

// Table Definition
object ChargePointTransactionTable : LongIdTable("charge_point_transaction") {
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
        objectMapper = MontaSerialization.getDefaultMapper(),
    ).nullable()
    var createdAt = timestamp("created_at").default(Instant.now())
}

// DAO
class ChargePointTransactionDAO(
    id: EntityID<Long>,
) : LongEntity(id), Loggable {
    companion object : LongEntityClass<ChargePointTransactionDAO>(ChargePointTransactionTable) {
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
            endReasonDescription: String? = null,
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

    var chargePointId by ChargePointTransactionTable.chargePointId
    var chargePoint by ChargePointDAO referencedOn ChargePointTransactionTable.chargePointId
    var chargePointConnector by ChargePointConnectorDAO referencedOn ChargePointTransactionTable.connectorId

    var connectorPosition by ChargePointTransactionTable.connectorPosition
    var externalId by ChargePointTransactionTable.externalId
    var idTag by ChargePointTransactionTable.idTag
    var statusAt by ChargePointTransactionTable.statusAt
    var startMeter by ChargePointTransactionTable.startMeter
    var startTime by ChargePointTransactionTable.startTime
    var meterValuesAt by ChargePointTransactionTable.meterValuesAt
    var endMeter by ChargePointTransactionTable.endMeter
    var endMeterAt by ChargePointTransactionTable.endMeterAt
    var endTime by ChargePointTransactionTable.endTime
    var endReason by ChargePointTransactionTable.endReason
    var endReasonDescription by ChargePointTransactionTable.endReasonDescription
    var chargingProfile by ChargePointTransactionTable.chargingProfile
    var createdAt by ChargePointTransactionTable.createdAt

    fun isOwner(
        connector: ChargePointConnectorDAO,
    ): Boolean {
        return this.chargePointId == connector.chargePointId
    }

    fun isOwner(
        chargePoint: ChargePointDAO,
    ): Boolean {
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
        return ChargingProfileCalculator.getWatts(
            chargingProfile = chargingProfile,
            transactionStartedAt = createdAt,
        )
    }

    fun clearChargingProfile() {
        transaction {
            chargingProfile = null
        }
    }
}
