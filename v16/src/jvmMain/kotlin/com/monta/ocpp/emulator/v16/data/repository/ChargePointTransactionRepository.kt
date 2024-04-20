package com.monta.ocpp.emulator.v16.data.repository

import com.monta.ocpp.emulator.v16.data.entity.ChargePointTransaction
import com.monta.ocpp.emulator.v16.data.entity.ChargePointTransactionDAO
import org.koin.core.annotation.Singleton

@Singleton
class ChargePointTransactionRepository {
    fun getByExternalId(
        externalId: Int
    ): ChargePointTransactionDAO? {
        return ChargePointTransactionDAO.find {
            ChargePointTransaction.externalId eq externalId
        }.firstOrNull()
    }
}
