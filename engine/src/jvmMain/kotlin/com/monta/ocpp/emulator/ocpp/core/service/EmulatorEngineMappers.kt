package com.monta.ocpp.emulator.ocpp.core.service

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.PreviousMessagesDAO
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.ocpp.core.model.ActiveTransactionDto
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointConnectorDto
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointDto
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointListItemDto
import com.monta.ocpp.emulator.ocpp.core.model.PreviousMessageDto
import com.monta.ocpp.emulator.platform.database.extension.idValue
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * DAO → DTO projections that back [DefaultEmulatorEngine]'s `observe*` flows and query commands.
 *
 * Each runs inside a `transaction { }` because it traverses Exposed lazy relations (`connectors`,
 * `activeTransaction`, the `transactions` sum behind `meterWh`). Kept here in the facade's service
 * package rather than in `model/` so the plain DTOs never import an Exposed entity.
 *
 * [toListItemDto] is the cheap counterpart to [toDto]: scalar columns only, no connector traversal.
 */

internal fun ChargePointDAO.toListItemDto(): ChargePointListItemDto {
    return transaction {
        ChargePointListItemDto(
            id = idValue,
            name = name,
            identity = identity,
            ocppVersion = ocppVersion,
            ocppUrl = ocppUrl,
            operationMode = operationMode,
            maxKw = maxKw,
            connected = connected,
        )
    }
}

internal fun ChargePointDAO.toDto(): ChargePointDto {
    return transaction {
        ChargePointDto(
            id = idValue,
            name = name,
            identity = identity,
            ocppVersion = ocppVersion,
            ocppUrl = ocppUrl,
            apiUrl = apiUrl,
            operationMode = operationMode,
            maxKw = maxKw,
            connected = connected,
            status = status,
            statusAt = statusAt,
            averageLatencyMillis = averageLatencyMillis,
            messageCount = messageCount,
            firmware = firmware,
            firmwareStatus = firmwareStatus,
            diagnosticsStatus = diagnosticsStatus,
            errorCode = errorCode,
            displayText = displayText,
            brand = brand,
            model = model,
            serial = serial,
            meterType = meterType,
            basicAuthPassword = basicAuthPassword,
            meterValuesSampledData = configuration.meterValuesSampledData,
            connectors = getConnectors()
                .sortedBy { connector -> connector.position }
                .map { connector -> connector.toDto() },
        )
    }
}

internal fun ChargePointConnectorDAO.toDto(): ChargePointConnectorDto {
    return transaction {
        ChargePointConnectorDto(
            id = idValue,
            chargePointId = chargePointId.value,
            position = position,
            status = status,
            statusAt = statusAt,
            errorCode = errorCode,
            vendorId = vendorId,
            vendorErrorCode = vendorErrorCode,
            statusInfo = statusInfo,
            carState = carState,
            locked = locked,
            maxKw = maxKw,
            kw = kw,
            vehicleMaxAmpsPerPhase = vehicleMaxAmpsPerPhase,
            vehicleNumberPhases = vehicleNumberPhases,
            meterWh = meterWh,
            activeTransaction = activeTransaction?.toDto(),
        )
    }
}

internal fun ChargePointTransactionDAO.toDto(): ActiveTransactionDto {
    return transaction {
        ActiveTransactionDto(
            id = idValue,
            externalId = externalId,
            idTag = idTag,
            connectorPosition = connectorPosition,
            startTime = startTime,
            startMeter = startMeter,
            endMeter = endMeter,
        )
    }
}

internal fun PreviousMessagesDAO.toDto(): PreviousMessageDto {
    return PreviousMessageDto(
        id = idValue,
        messageType = messageType,
        message = message,
    )
}
