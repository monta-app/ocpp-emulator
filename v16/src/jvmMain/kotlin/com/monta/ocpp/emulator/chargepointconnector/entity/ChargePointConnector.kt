package com.monta.ocpp.emulator.chargepointconnector.entity

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepointconnector.model.CarState
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.logger.Loggable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import kotlin.math.ceil
import kotlin.math.min

// Table Definition
object ChargePointConnectorTable : LongIdTable("charge_point_connector") {
    val chargePointId = reference("charge_point_id", ChargePointTable)
    val position = integer("position")
    val chargePointIdentity = varchar("charge_point_identity", 512)
    val activeTransactionId = optReference("active_transaction_id", ChargePointTransaction)
    val status = enumerationByName("status", 128, ChargePointStatus::class)
    val statusAt = timestamp("status_at")
    val meterAt = timestamp("meter_at").nullable().default(null)
    val carState = enumerationByName("car_state", 128, CarState::class)
    val errorCode = enumerationByName("error_code", 256, ChargePointErrorCode::class)
    val maxKw = double("max_kw")
    val kw = double("kw")
    val vehicleMaxAmpsPerPhase = double("vehicle_max_amps_per_phase").default(1.0)
    val vehicleNumberPhases = integer("vehicle_number_phases").default(3)
    val locked = bool("locked")
}

// DAO
class ChargePointConnectorDAO(
    id: EntityID<Long>
) : LongEntity(id), Loggable {

    companion object : LongEntityClass<ChargePointConnectorDAO>(ChargePointConnectorTable) {
        fun newInstance(
            chargePointId: Long,
            chargePointIdentity: String,
            position: Int,
            status: ChargePointStatus,
            errorCode: ChargePointErrorCode,
            maxKw: Double
        ): ChargePointConnectorDAO {
            return ChargePointConnectorDAO.new {
                this.chargePointId = EntityID(chargePointId, ChargePointTable)
                this.chargePointIdentity = chargePointIdentity
                this.position = position
                this.status = status
                this.statusAt = Instant.now()
                this.meterAt = Instant.now()
                this.carState = CarState.C
                this.errorCode = errorCode
                this.maxKw = maxKw
                this.kw = maxKw
                this.vehicleMaxAmpsPerPhase = ceil(maxKw * 1000 / 230 / 3)
                this.vehicleNumberPhases = 3
                this.locked = false
            }
        }
    }

    var chargePointId by ChargePointConnectorTable.chargePointId
    var position by ChargePointConnectorTable.position
    var chargePointIdentity by ChargePointConnectorTable.chargePointIdentity
    var activeTransactionId by ChargePointConnectorTable.activeTransactionId
    var activeTransaction by ChargePointTransactionDAO optionalReferencedOn ChargePointConnectorTable.activeTransactionId
    var status by ChargePointConnectorTable.status
    var statusAt by ChargePointConnectorTable.statusAt
    var meterAt by ChargePointConnectorTable.meterAt
    var carState by ChargePointConnectorTable.carState
    var errorCode by ChargePointConnectorTable.errorCode
    var maxKw by ChargePointConnectorTable.maxKw
    var kw by ChargePointConnectorTable.kw
    var vehicleMaxAmpsPerPhase by ChargePointConnectorTable.vehicleMaxAmpsPerPhase
    var vehicleNumberPhases by ChargePointConnectorTable.vehicleNumberPhases
    var locked by ChargePointConnectorTable.locked

    val transactions: List<ChargePointTransactionDAO> by lazy {
        transaction {
            ChargePointTransactionDAO.find {
                (ChargePointTransaction.chargePointId eq chargePointId) and
                    (ChargePointTransaction.connectorId eq this@ChargePointConnectorDAO.id)
            }.toList()
        }
    }

    val activeTransactions: List<ChargePointTransactionDAO> by lazy {
        transaction {
            ChargePointTransactionDAO.find {
                (ChargePointTransaction.chargePointId eq chargePointId) and
                    (ChargePointTransaction.connectorId eq this@ChargePointConnectorDAO.id) and
                    (ChargePointTransaction.endTime eq null)
            }.toList()
        }
    }

    val sessionInfo: OcppSession.Info by lazy {
        OcppSession.Info("", chargePointIdentity)
    }

    val meterWh: Double
        get() = transactions.sumOf { it.endMeter }
    val wattHoursPerSecond: Double
        get() = (kw / 60.0 / 60.0) * 1000.0
    val hasActiveTransaction: Boolean
        get() = activeTransactions.isNotEmpty()

    override fun chargePointId(): Long {
        return this.chargePointId.value
    }

    override fun connectorPosition(): Int {
        return position
    }

    fun calculateState(justStopped: Boolean = false): ChargePointStatus {
        return when (carState) {
            CarState.A -> ChargePointStatus.Available

            CarState.B -> if (transaction { activeTransaction } != null) {
                getSuspendedState() ?: ChargePointStatus.SuspendedEV
            } else {
                if (justStopped) ChargePointStatus.Finishing else ChargePointStatus.Preparing
            }

            CarState.C -> if (transaction { activeTransaction } != null) {
                getSuspendedState() ?: ChargePointStatus.Charging
            } else {
                if (justStopped) ChargePointStatus.Finishing else ChargePointStatus.Preparing
            }
        }
    }

    private fun getSuspendedState(): ChargePointStatus? {
        if (kw == 0.0) {
            return ChargePointStatus.SuspendedEVSE
        }
        return null
    }

    fun getChargePoint(): ChargePointDAO {
        return transaction {
            ChargePointDAO.find {
                ChargePointTable.id eq chargePointId
            }.first()
        }
    }

    fun updateKw(chargingProfileWatts: Double? = null) {
        val maxWatts = min(chargingProfileWatts ?: (maxKw * 1000), maxKw * 1000)
        val wattsPerPhase = maxWatts / 3
        val vehicleMaxWattsPerPhase = vehicleMaxAmpsPerPhase * 230
        val actualKwPerPhase = min(wattsPerPhase, vehicleMaxWattsPerPhase) / 1000
        this.kw = actualKwPerPhase * vehicleNumberPhases
    }
}
