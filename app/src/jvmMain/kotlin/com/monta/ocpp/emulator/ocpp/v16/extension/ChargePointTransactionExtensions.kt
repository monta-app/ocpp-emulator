package com.monta.ocpp.emulator.ocpp.v16.extension

import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
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
