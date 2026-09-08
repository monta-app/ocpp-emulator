package com.monta.ocpp.emulator.chargepoint.connector.repository

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import javax.inject.Singleton

@Singleton
class ChargePointConnectorRepository {
    fun getById(
        id: Long,
    ): ChargePointConnectorDAO? {
        return ChargePointConnectorDAO.find {
            (ChargePointConnectorTable.id eq id)
        }.firstOrNull()
    }

    fun getByPosition(
        chargePointId: Long,
        connectorId: Int,
    ): ChargePointConnectorDAO? {
        return ChargePointConnectorDAO.find {
            (ChargePointConnectorTable.chargePointId eq chargePointId) and
                (ChargePointConnectorTable.position eq connectorId)
        }.firstOrNull()
    }
}
