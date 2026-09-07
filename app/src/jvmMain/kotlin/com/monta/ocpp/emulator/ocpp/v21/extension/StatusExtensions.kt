package com.monta.ocpp.emulator.ocpp.v21.extension

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.ocpp.v21.protocol.client.OcppClientV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.StatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ConnectorStatusEnum
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.time.ZonedDateTime

fun ChargePointStatus.toOcpp21ConnectorStatus(
    carState: CarState? = null,
    hasTx: Boolean = false,
): ConnectorStatusEnum {
    val mapped = when (this) {
        ChargePointStatus.Available -> if (carState != null && carState != CarState.A) {
            ConnectorStatusEnum.Occupied
        } else {
            ConnectorStatusEnum.Available
        }
        ChargePointStatus.Preparing,
        ChargePointStatus.Charging,
        ChargePointStatus.SuspendedEV,
        ChargePointStatus.SuspendedEVSE,
        ChargePointStatus.Finishing,
        -> ConnectorStatusEnum.Occupied
        ChargePointStatus.Reserved -> ConnectorStatusEnum.Reserved
        ChargePointStatus.Unavailable -> ConnectorStatusEnum.Unavailable
        ChargePointStatus.Faulted -> ConnectorStatusEnum.Faulted
    }
    return if (hasTx && mapped == ConnectorStatusEnum.Available) {
        ConnectorStatusEnum.Occupied
    } else {
        mapped
    }
}

suspend fun ChargePointConnectorDAO.setStatus21(
    status: ChargePointStatus,
    errorCode: ChargePointErrorCode = ChargePointErrorCode.NoError,
    forceUpdate: Boolean = false,
) {
    if (!forceUpdate && this.status == status) {
        return
    }
    transaction {
        this@setStatus21.status = status
        this@setStatus21.statusAt = Instant.now()
        this@setStatus21.errorCode = errorCode
    }
    val ocppClientV21: OcppClientV21 by injectAnywhere()
    ocppClientV21.asAvailability(sessionInfo).statusNotification(
        StatusNotificationRequest(
            connectorId = 1,
            connectorStatus = status.toOcpp21ConnectorStatus(carState, hasActiveTransaction),
            evseId = position,
            timestamp = ZonedDateTime.now(),
        ),
    )
    GlobalLogger.info(this, "OCPP 2.1 status set to $status")
}

suspend fun ChargePointDAO.setStatus21(
    status: ChargePointStatus,
    errorCode: ChargePointErrorCode = ChargePointErrorCode.NoError,
) {
    transaction {
        this@setStatus21.status = status
        this@setStatus21.statusAt = Instant.now()
        this@setStatus21.errorCode = errorCode
    }
}
