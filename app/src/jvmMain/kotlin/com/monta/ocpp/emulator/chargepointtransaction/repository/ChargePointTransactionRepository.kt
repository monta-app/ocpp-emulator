package com.monta.ocpp.emulator.chargepointtransaction.repository

import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
import org.jetbrains.exposed.v1.core.eq
import javax.inject.Singleton

@Singleton
class ChargePointTransactionRepository {
    fun getByExternalId(
        externalId: Int,
    ): ChargePointTransactionDAO? {
        return ChargePointTransactionDAO.find {
            ChargePointTransaction.externalId eq externalId
        }.firstOrNull()
    }
}
