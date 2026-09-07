package com.monta.ocpp.emulator.ocpp.v201.extension

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v201.blocks.availability.StatusNotificationRequest
import com.monta.library.ocpp.v201.client.OcppClientV201
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.time.ZonedDateTime

fun ChargePointStatus.toOcpp201ConnectorStatus(
    carState: CarState? = null,
    hasTx: Boolean = false,
): StatusNotificationRequest.ConnectorStatus {
    val mapped = when (this) {
        ChargePointStatus.Available ->
            if (carState != null && carState != CarState.A) {
                StatusNotificationRequest.ConnectorStatus.Occupied
            } else {
                StatusNotificationRequest.ConnectorStatus.Available
            }
        ChargePointStatus.Preparing,
        ChargePointStatus.Charging,
        ChargePointStatus.SuspendedEV,
        ChargePointStatus.SuspendedEVSE,
        ChargePointStatus.Finishing,
        -> StatusNotificationRequest.ConnectorStatus.Occupied
        ChargePointStatus.Reserved -> StatusNotificationRequest.ConnectorStatus.Reserved
        ChargePointStatus.Unavailable -> StatusNotificationRequest.ConnectorStatus.Unavailable
        ChargePointStatus.Faulted -> StatusNotificationRequest.ConnectorStatus.Faulted
    }
    return if (hasTx && mapped == StatusNotificationRequest.ConnectorStatus.Available) {
        StatusNotificationRequest.ConnectorStatus.Occupied
    } else {
        mapped
    }
}

suspend fun ChargePointConnectorDAO.setStatus201(
    status: ChargePointStatus,
    errorCode: ChargePointErrorCode = ChargePointErrorCode.NoError,
    forceUpdate: Boolean = false,
) {
    if (!forceUpdate && this.status == status) {
        return
    }
    transaction {
        this@setStatus201.status = status
        this@setStatus201.statusAt = Instant.now()
        this@setStatus201.errorCode = errorCode
    }
    val client: OcppClientV201 by injectAnywhere()
    client.asAvailability(sessionInfo).statusNotification(
        StatusNotificationRequest(
            timestamp = ZonedDateTime.now(),
            connectorStatus = status.toOcpp201ConnectorStatus(carState, hasActiveTransaction),
            evseId = position.toLong(),
            connectorId = 1L,
        ),
    )
    GlobalLogger.info(this, "OCPP 2.0.1 status set to $status")
}

suspend fun ChargePointDAO.setStatus201(
    status: ChargePointStatus,
    errorCode: ChargePointErrorCode = ChargePointErrorCode.NoError,
) {
    transaction {
        this@setStatus201.status = status
        this@setStatus201.statusAt = Instant.now()
        this@setStatus201.errorCode = errorCode
    }
}
