package com.monta.ocpp.emulator.v16.data.entity

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.common.util.MontaSerialization
import com.monta.ocpp.emulator.common.util.json
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object TxDefault : LongIdTable("charge_point_default_profile") {
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
    companion object : LongEntityClass<TxDefaultDAO>(TxDefault) {
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

    var chargePoint by ChargePointDAO referencedOn TxDefault.chargePointId
    var connector by ChargePointConnectorDAO referencedOn TxDefault.connectorId
    var chargingProfileId by TxDefault.chargingProfileId
    var stackLevel by TxDefault.stackLeveL
    var txDefaultProfile by TxDefault.txDefaultProfile
}
