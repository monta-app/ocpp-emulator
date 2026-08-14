package com.monta.ocpp.emulator.control.model

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState

data class IdentityParams(
    val identity: String,
)

data class ConnectorIdentityParams(
    val identity: String,
    val connectorId: Int = 1,
)

data class SetAvailabilityParams(
    val identity: String,
    val status: ChargePointStatus,
)

data class SetCarStateParams(
    val identity: String,
    val carState: CarState,
    val connectorId: Int = 1,
)

data class SetConnectorStatusParams(
    val identity: String,
    val status: ChargePointStatus,
    val connectorId: Int = 1,
    val errorCode: ChargePointErrorCode = ChargePointErrorCode.NoError,
    val vendorId: String? = null,
    val vendorErrorCode: String? = null,
    val info: String? = null,
)

data class AuthorizeParams(
    val identity: String,
    val idTag: String,
    val connectorId: Int = 1,
)

data class StopTransactionParams(
    val identity: String,
    val connectorId: Int = 1,
    val reason: Reason = Reason.Local,
    val endReasonDescription: String? = null,
)
