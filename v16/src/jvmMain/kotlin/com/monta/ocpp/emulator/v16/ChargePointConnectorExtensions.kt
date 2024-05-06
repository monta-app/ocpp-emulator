package com.monta.ocpp.emulator.v16

import com.monta.library.ocpp.v16.AuthorizationStatus
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.library.ocpp.v16.core.StartTransactionConfirmation
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.model.CarState
import com.monta.ocpp.emulator.chargepointtransaction.service.ChargePointTransactionService
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.logger.GlobalLogger
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

private val logger = KotlinLogging.logger {}

suspend fun ChargePointConnectorDAO.setStatuses(
    vararg statuses: ChargePointStatus
) {
    for (status in statuses) {
        setStatus(
            status = status
        )
        delay(300)
    }
}

suspend fun ChargePointConnectorDAO.setStatus(
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
        this@setStatus.errorCode = errorCode
    }

    statusNotification(
        sessionInfo = this.sessionInfo,
        connectorId = this.position,
        status = status,
        errorCode = errorCode
    )

    GlobalLogger.info(this, "Status set to $status")

    startFreeCharging()
}

suspend fun ChargePointConnectorDAO.startFreeCharging() {
    // Already charging don't try
    if (hasActiveTransaction) {
        return
    }

    // Only start a free charge if charge point is in the right state
    if (status != ChargePointStatus.Preparing) {
        return
    }

    val chargePoint = getChargePoint()

    // If free charging isn't enabled don't try
    if (!chargePoint.configuration.freeCharging) {
        return
    }

    // Start the charge automatically with the configured `freeChargingIdTag`
    start(chargePoint.configuration.freeChargingIdTag)
}

suspend fun ChargePointConnectorDAO.start(
    idTag: String
) {
    GlobalLogger.info(this, "Attempting to start charge")

    val confirmation = startTransaction(
        sessionInfo = sessionInfo,
        connector = this,
        idTag = idTag
    )

    when (confirmation.idTagInfo.status) {
        AuthorizationStatus.Accepted -> {
            onStartSuccess(idTag, confirmation)
        }

        AuthorizationStatus.Blocked,
        AuthorizationStatus.Expired,
        AuthorizationStatus.Invalid,
        AuthorizationStatus.ConcurrentTx
        -> {
            GlobalLogger.warn(
                this,
                "Unable to start charge reason=${confirmation.idTagInfo.status}"
            )
        }
    }
}

private suspend fun ChargePointConnectorDAO.onStartSuccess(
    idTag: String,
    confirmation: StartTransactionConfirmation
) {
    try {
        GlobalLogger.info(this, "Charge started")

        val chargePointTransactionService: ChargePointTransactionService by injectAnywhere()

        val chargePointTransaction = chargePointTransactionService.create(
            chargePoint = getChargePoint(),
            chargePointConnector = this,
            externalId = confirmation.transactionId,
            idTag = idTag
        )

        transaction {
            this@onStartSuccess.activeTransaction = chargePointTransaction
        }

        // If our car is in State A we want to make sure it's plugged in when starting a new
        // transaction so we will manually flip it over in order to avoid any confusion
        if (this.carState == CarState.A) {
            transaction {
                this@onStartSuccess.carState = CarState.B
            }
        }

        GlobalLogger.info(this, "Charge started with id ${confirmation.transactionId}")

        // Start the notification sequence
        setStatuses(
            ChargePointStatus.Preparing,
            // The last notification sent should be calculated on the connector state
            calculateState()
        )
    } catch (exception: Exception) {
        logger.warn("Failed to start charge", exception)
    }
}

suspend fun ChargePointConnectorDAO.stopActiveTransactions(
    reason: Reason?,
    endReasonDescription: String? = null
) {
    if (activeTransactions.isEmpty()) {
        logger.warn { "no active transaction found" }
        return
    }

    for (activeTransaction in activeTransactions) {
        activeTransaction.stop(reason, endReasonDescription)
    }

    logger.info { "stopped all active transactions" }
}

suspend fun ChargePointConnectorDAO.setConnectorCarState(
    carState: CarState
) {
    transaction {
        this@setConnectorCarState.carState = carState
    }

    calculateAndSetState()
}

suspend fun ChargePointConnectorDAO.setMaxVehicleRate(
    amps: Double
) {
    transaction {
        this@setMaxVehicleRate.vehicleMaxAmpsPerPhase = amps
    }

    calculateAndSetState()
}

suspend fun ChargePointConnectorDAO.setNumberPhases(
    numberPhases: Int
) {
    transaction {
        this@setNumberPhases.vehicleNumberPhases = numberPhases
    }
}

private suspend fun ChargePointConnectorDAO.calculateAndSetState() {
    val chargePointStatus = calculateState()

    if (chargePointStatus == ChargePointStatus.Available) {
        stopActiveTransactions(Reason.Local)
    }

    setStatus(chargePointStatus)
}
