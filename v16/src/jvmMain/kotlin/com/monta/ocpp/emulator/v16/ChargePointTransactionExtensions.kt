package com.monta.ocpp.emulator.v16

import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.logger.GlobalLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

suspend fun ChargePointTransactionDAO.stop(
    reason: Reason?,
    endReasonDescription: String? = null,
) {
    val connector = transaction { chargePointConnector }

    GlobalLogger.info(this, "Attempting to stop charge")

    stopTransaction(
        sessionInfo = connector.sessionInfo,
        transaction = this,
        reason = reason,
    )

    transaction {
        // Set connector values
        connector.activeTransaction = null
        // Set transaction values
        this@stop.endTime = Instant.now()
        this@stop.endReason = reason
        this@stop.endReasonDescription = endReasonDescription
    }

    GlobalLogger.info(connector, "Charge was stopped")

    connector.setStatuses(
        connector.calculateState(true),
    )
}
