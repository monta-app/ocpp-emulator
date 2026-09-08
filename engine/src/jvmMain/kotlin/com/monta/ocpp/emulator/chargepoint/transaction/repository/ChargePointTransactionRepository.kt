package com.monta.ocpp.emulator.chargepoint.transaction.repository

import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionTable
import org.jetbrains.exposed.v1.core.eq
import javax.inject.Singleton

@Singleton
class ChargePointTransactionRepository {
    fun getByExternalId(
        externalId: Int,
    ): ChargePointTransactionDAO? {
        return ChargePointTransactionDAO.find {
            ChargePointTransactionTable.externalId eq externalId
        }.firstOrNull()
    }
}
