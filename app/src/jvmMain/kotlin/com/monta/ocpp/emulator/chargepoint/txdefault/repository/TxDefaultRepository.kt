package com.monta.ocpp.emulator.chargepoint.txdefault.repository

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingProfilePurposeType
import com.monta.library.ocpp.v16.smartcharge.ClearChargingProfileRequest
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.txdefault.entity.TxDefault
import com.monta.ocpp.emulator.chargepoint.txdefault.entity.TxDefaultDAO
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import javax.inject.Singleton

@Singleton
class TxDefaultRepository {

    fun store(
        chargePointDAO: ChargePointDAO,
        connectorDAO: ChargePointConnectorDAO,
        chargingProfile: ChargingProfile,
    ): TxDefaultDAO {
        require(
            chargingProfile.chargingProfilePurpose == ChargingProfilePurposeType.TxDefaultProfile,
        ) {
            "chargingProfile must be a TxDefaultProfile"
        }
        // v1.6 section 7.8:ChargingProfile.chargingProfileId is required
        require(chargingProfile.chargingProfileId != null) {
            "chargingProfileId is required"
        }
        val item = findById(chargePointDAO, connectorDAO, chargingProfile.chargingProfileId!!)
        return if (item != null) {
            item.txDefaultProfile = chargingProfile
            item
        } else {
            TxDefaultDAO.newInstance(chargePointDAO, connectorDAO, chargingProfile)
        }
    }

    fun delete(
        chargePointDAO: ChargePointDAO,
        connectorDAO: ChargePointConnectorDAO?,
        request: ClearChargingProfileRequest,
    ) {
        /*
         v1.6 section 6.13:
         The Central System can use this message to clear (remove) either a specific charging profile (denoted by id)
         or
         a selection of charging profiles that match with the values of the
         optional connectorId, stackLevel and chargingProfilePurpose fields.

         The ClearChargingProfileRequest does not contain stack level.
         */

        val onChargePoint = TxDefault.chargePointId eq chargePointDAO.chargePointId()
        val onConnector = connectorDAO?.let { connector -> TxDefault.connectorId eq connector.id } ?: Op.TRUE
        val condition = when {
            request.id != null -> onChargePoint and (TxDefault.chargingProfileId eq request.id)
            else -> onChargePoint and onConnector
        }

        TxDefault.deleteWhere { condition }
    }

    private fun findById(
        chargePointDAO: ChargePointDAO,
        connectorDAO: ChargePointConnectorDAO,
        chargingProfileId: Int,
    ): TxDefaultDAO? {
        val equalsProfileId = TxDefault.chargingProfileId eq chargingProfileId
        val onChargePoint = TxDefault.chargePointId eq chargePointDAO.chargePointId()
        val onConnector = TxDefault.connectorId eq connectorDAO.id
        return TxDefaultDAO.find { onChargePoint and onConnector and equalsProfileId }.firstOrNull()
    }
}
