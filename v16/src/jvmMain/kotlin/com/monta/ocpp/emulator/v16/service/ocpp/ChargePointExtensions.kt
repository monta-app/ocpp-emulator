package com.monta.ocpp.emulator.v16.service.ocpp

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.logger.GlobalLogger
import com.monta.ocpp.emulator.v16.data.entity.ChargePointDAO
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

suspend fun ChargePointDAO.setStatuses(
    vararg statuses: ChargePointStatus
) {
    for (status in statuses) {
        setStatus(
            status = status
        )
        delay(300)
    }
}

suspend fun ChargePointDAO.setStatus(
    status: ChargePointStatus,
    errorCode: ChargePointErrorCode = ChargePointErrorCode.NoError,
    forceUpdate: Boolean = false
) {
    if (!forceUpdate && this.status == status) {
        // Don't publish the same state
        return
    }

    transaction {
        this@setStatus.status = status
        this@setStatus.statusAt = Instant.now()
    }

    statusNotification(
        sessionInfo = sessionInfo,
        connectorId = 0,
        status = status,
        errorCode = errorCode
    )

    GlobalLogger.info(this, "Status set to $status")
}
