package com.monta.ocpp.emulator.chargepointtransaction.repository

import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
import org.jetbrains.exposed.sql.and
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

    fun getActiveByExternalIdAndChargePointId(
        externalId: Int,
        chargePointId: Long,
    ): ChargePointTransactionDAO? {
        return ChargePointTransactionDAO.find {
            (ChargePointTransaction.externalId eq externalId) and
                (ChargePointTransaction.chargePointId eq chargePointId) and
                (ChargePointTransaction.endTime eq null)
        }.firstOrNull()
    }
}
