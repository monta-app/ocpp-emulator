package com.monta.ocpp.emulator.chargepoint.connector.service

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.repository.ChargePointConnectorRepository
import com.monta.ocpp.emulator.platform.database.extension.createDatabaseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
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
        id: Long,
    ): Flow<ChargePointConnectorDAO> {
        return createDatabaseListener(
            entityClass = ChargePointConnectorDAO,
            id = id,
        ) {
            transaction {
                chargePointConnectorRepository.getById(id)
            }
        }
    }

    /**
     * Source-compatibility shim for the Compose UI, which still passes its own [CoroutineScope];
     * the scope is no longer needed, so this delegates to the no-arg [getByIdFlow].
     */
    @Suppress("UNUSED_PARAMETER")
    fun getByIdFlow(
        coroutineScope: CoroutineScope,
        id: Long,
    ): Flow<ChargePointConnectorDAO> = getByIdFlow(id)

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
