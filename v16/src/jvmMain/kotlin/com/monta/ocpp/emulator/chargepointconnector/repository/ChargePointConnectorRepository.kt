package com.monta.ocpp.emulator.chargepointconnector.repository

import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorTable
import org.jetbrains.exposed.sql.and
import org.koin.core.annotation.Singleton

@Singleton
class ChargePointConnectorRepository {
    fun getById(
        id: Long
    ): ChargePointConnectorDAO? {
        return ChargePointConnectorDAO.find {
            (ChargePointConnectorTable.id eq id)
        }.firstOrNull()
    }

    fun getByPosition(
        chargePointId: Long,
        connectorId: Int
    ): ChargePointConnectorDAO? {
        return ChargePointConnectorDAO.find {
            (ChargePointConnectorTable.chargePointId eq chargePointId) and
                (ChargePointConnectorTable.position eq connectorId)
        }.firstOrNull()
    }
}
