package com.monta.ocpp.emulator.v16.data.service

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingProfilePurposeType
import com.monta.library.ocpp.v16.smartcharge.ClearChargingProfileRequest
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.v16.data.entity.TxDefaultDAO
import com.monta.ocpp.emulator.v16.data.repository.TxDefaultRepository
import org.jetbrains.exposed.sql.transactions.transaction
import javax.inject.Singleton

@Singleton
class TxDefaultService(
    private val txDefaultRepository: TxDefaultRepository,
) {

    fun store(
        chargePoint: ChargePointDAO,
        chargePointConnector: ChargePointConnectorDAO,
        txProfile: ChargingProfile,
    ): TxDefaultDAO {
        return transaction {
            txDefaultRepository.store(chargePoint, chargePointConnector, txProfile)
        }
    }

    fun clear(
        chargePoint: ChargePointDAO,
        connectorDAO: ChargePointConnectorDAO?,
        request: ClearChargingProfileRequest,
    ) {
        if (request.chargingProfilePurpose == null ||
            request.chargingProfilePurpose == ChargingProfilePurposeType.TxDefaultProfile
        ) {
            return transaction {
                txDefaultRepository.delete(chargePoint, connectorDAO, request)
            }
        }
    }
}
