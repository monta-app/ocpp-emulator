package com.monta.ocpp.emulator.chargepointconnector.service

import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.repository.ChargePointConnectorRepository
import com.monta.ocpp.emulator.common.createDatabaseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.sql.transactions.transaction
import javax.inject.Singleton

@Singleton
class ChargePointConnectorService(
    private val chargePointConnectorRepository: ChargePointConnectorRepository,
) {

    fun get(
        chargePointId: Long,
        connectorId: Int,
    ): ChargePointConnectorDAO? {
        return transaction {
            chargePointConnectorRepository.getByPosition(
                chargePointId = chargePointId,
                connectorId = connectorId,
            )
        }
    }

    fun getByIdFlow(
        coroutineScope: CoroutineScope,
        id: Long,
    ): Flow<ChargePointConnectorDAO> {
        return createDatabaseListener(
            coroutineScope = coroutineScope,
            entityClass = ChargePointConnectorDAO,
            id = id,
        ) {
            transaction {
                chargePointConnectorRepository.getById(id)
            }
        }
    }

    fun update(
        connector: ChargePointConnectorDAO,
        block: ChargePointConnectorDAO.() -> Unit,
    ): ChargePointConnectorDAO {
        return transaction {
            transaction {
                block(connector)
            }
            connector
        }
    }
}
