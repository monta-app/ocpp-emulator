package com.monta.ocpp.emulator.chargepoint.txdefault.entity

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointTable
import com.monta.ocpp.emulator.platform.util.MontaSerialization
import com.monta.ocpp.emulator.platform.util.json
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

object TxDefaultTable : LongIdTable("charge_point_default_profile") {
    val chargePointId = reference("charge_point_id", ChargePointTable)
    val connectorId = reference("connector_id", ChargePointConnectorTable)

    val chargingProfileId = integer("charging_profile_id").nullable()
    val stackLeveL = integer("stack_level").nullable()
    val txDefaultProfile = json<ChargingProfile>(
        name = "tx_default_profile",
        objectMapper = MontaSerialization.getDefaultMapper(),
    )
}

// DAO
class TxDefaultDAO(
    id: EntityID<Long>,
) : LongEntity(id) {
    companion object : LongEntityClass<TxDefaultDAO>(TxDefaultTable) {
        fun newInstance(
            chargePoint: ChargePointDAO,
            chargePointConnector: ChargePointConnectorDAO,
            chargingProfile: ChargingProfile,
        ): TxDefaultDAO {
            return TxDefaultDAO.new {
                this.chargePoint = chargePoint
                this.connector = chargePointConnector
                this.chargingProfileId = chargingProfile.chargingProfileId
                this.stackLevel = chargingProfile.stackLevel
                this.txDefaultProfile = chargingProfile
            }
        }
    }

    var chargePoint by ChargePointDAO referencedOn TxDefaultTable.chargePointId
    var connector by ChargePointConnectorDAO referencedOn TxDefaultTable.connectorId
    var chargingProfileId by TxDefaultTable.chargingProfileId
    var stackLevel by TxDefaultTable.stackLeveL
    var txDefaultProfile by TxDefaultTable.txDefaultProfile
}
