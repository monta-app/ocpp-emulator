package com.monta.ocpp.emulator.chargepoint.transaction.service

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.chargepoint.transaction.repository.ChargePointTransactionRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.inject.Singleton

@Singleton
class ChargePointTransactionService(
    private val chargePointTransactionRepository: ChargePointTransactionRepository,
) {

    fun create(
        chargePoint: ChargePointDAO,
        chargePointConnector: ChargePointConnectorDAO,
        externalId: Int,
        idTag: String,
    ): ChargePointTransactionDAO {
        return transaction {
            ChargePointTransactionDAO.newInstance(
                chargePoint = chargePoint,
                chargePointConnector = chargePointConnector,
                externalId = externalId,
                idTag = idTag,
            )
        }
    }

    fun getByExternalId(
        externalId: Int,
    ): ChargePointTransactionDAO? {
        return transaction {
            chargePointTransactionRepository.getByExternalId(externalId)
        }
    }

    fun getByOcppTransactionId(
        ocppTransactionId: String,
    ): ChargePointTransactionDAO? {
        return transaction {
            chargePointTransactionRepository.getByOcppTransactionId(ocppTransactionId)
        }
    }

    fun update(
        externalId: Int,
        block: ChargePointTransactionDAO.() -> Unit,
    ): ChargePointTransactionDAO? {
        return transaction {
            val chargePointTransaction = getByExternalId(externalId)
            chargePointTransaction?.let { block(chargePointTransaction) }
            chargePointTransaction
        }
    }
}
