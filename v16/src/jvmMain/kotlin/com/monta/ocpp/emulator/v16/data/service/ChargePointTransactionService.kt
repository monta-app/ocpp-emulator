package com.monta.ocpp.emulator.v16.data.service

import com.monta.ocpp.emulator.v16.data.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.v16.data.entity.ChargePointDAO
import com.monta.ocpp.emulator.v16.data.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.v16.data.repository.ChargePointTransactionRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class ChargePointTransactionService(
    private val chargePointTransactionRepository: ChargePointTransactionRepository
) {

    fun create(
        chargePoint: ChargePointDAO,
        chargePointConnector: ChargePointConnectorDAO,
        externalId: Int,
        idTag: String
    ): ChargePointTransactionDAO {
        return transaction {
            ChargePointTransactionDAO.newInstance(
                chargePoint = chargePoint,
                chargePointConnector = chargePointConnector,
                externalId = externalId,
                idTag = idTag
            )
        }
    }

    fun getByExternalId(
        externalId: Int
    ): ChargePointTransactionDAO? {
        return transaction {
            chargePointTransactionRepository.getByExternalId(externalId)
        }
    }

    fun update(
        externalId: Int,
        block: ChargePointTransactionDAO.() -> Unit
    ): ChargePointTransactionDAO? {
        return transaction {
            val chargePointTransaction = getByExternalId(externalId)
            chargePointTransaction?.let { block(chargePointTransaction) }
            chargePointTransaction
        }
    }
}
