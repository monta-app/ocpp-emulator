package com.monta.ocpp.emulator.chargepoint.transaction.repository

import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import javax.inject.Singleton

@Singleton
class ChargePointTransactionRepository {
    // CSMS-assigned externalIds get reused across sessions, so only an open transaction (endTime == null) is a valid match.
    fun getByExternalId(
        externalId: Int,
    ): ChargePointTransactionDAO? {
        return ChargePointTransactionDAO.find {
            (ChargePointTransaction.externalId eq externalId) and
                (ChargePointTransaction.endTime eq null)
        }.firstOrNull()
    }
}
