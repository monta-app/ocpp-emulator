package com.monta.ocpp.emulator.control.model

import com.monta.library.ocpp.v16.AuthorizationStatus
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState

data class HelloResult(
    val emulatorVersion: String,
    val protocolVersion: Int = 1,
)

data class ChargePointStateResult(
    val identity: String,
    val connected: Boolean,
    val status: ChargePointStatus,
    val errorCode: ChargePointErrorCode,
    val connectors: List<ConnectorStateResult>,
)

data class ConnectorStateResult(
    val connectorId: Int,
    val status: ChargePointStatus,
    val errorCode: ChargePointErrorCode,
    val carState: CarState,
    val activeTransactionId: Long?,
    val meterWh: Double,
    val locked: Boolean,
)

data class AuthorizeResult(
    val status: AuthorizationStatus,
)
