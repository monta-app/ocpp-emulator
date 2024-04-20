package com.monta.ocpp.emulator.v16.service.ocpp

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.SampledValue
import com.monta.library.ocpp.v16.ValueFormat
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.MeterValue
import com.monta.library.ocpp.v16.core.Reason
import com.monta.library.ocpp.v16.core.StartTransactionConfirmation
import com.monta.library.ocpp.v16.core.StartTransactionRequest
import com.monta.library.ocpp.v16.core.StatusNotificationRequest
import com.monta.library.ocpp.v16.core.StopTransactionRequest
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.eichrecht.EichrechtSignatureService
import com.monta.ocpp.emulator.v16.data.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.v16.data.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.v16.data.util.idValue
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger {}

suspend fun statusNotification(
    sessionInfo: OcppSession.Info,
    connectorId: Int,
    status: ChargePointStatus,
    errorCode: ChargePointErrorCode,
    info: String? = null,
    timestamp: ZonedDateTime = ZonedDateTime.now(),
    vendorId: String? = null,
    vendorErrorCode: String? = null
) {
    val ocppClientV16: OcppClientV16 by injectAnywhere()

    try {
        ocppClientV16.asCoreProfile(sessionInfo).statusNotification(
            StatusNotificationRequest(
                connectorId = connectorId,
                errorCode = errorCode,
                info = info,
                status = status,
                timestamp = timestamp,
                vendorId = vendorId,
                vendorErrorCode = vendorErrorCode
            )
        )
    } catch (exception: Exception) {
        logger.warn("Failed to send status notification", exception)
    }
}

suspend fun startTransaction(
    sessionInfo: OcppSession.Info,
    connector: ChargePointConnectorDAO,
    idTag: String
): StartTransactionConfirmation {
    val ocppClientV16: OcppClientV16 by injectAnywhere()

    try {
        return ocppClientV16.asCoreProfile(sessionInfo).startTransaction(
            StartTransactionRequest(
                connectorId = connector.position,
                idTag = idTag,
                meterStart = 0,
                timestamp = ZonedDateTime.now()
            )
        )
    } catch (exception: Exception) {
        logger.warn("Failed to stop charge", exception)
        throw exception
    }
}

suspend fun stopTransaction(
    sessionInfo: OcppSession.Info,
    transaction: ChargePointTransactionDAO,
    reason: Reason?
) {
    val ocppClientV16: OcppClientV16 by injectAnywhere()

    val ocmf = transaction {
        val chargePoint = transaction.chargePoint
        val signaturService = EichrechtSignatureService(
            chargePointIdentity = chargePoint.identity,
            brand = chargePoint.brand,
            model = chargePoint.model,
            serial = chargePoint.serial,
            firmware = chargePoint.firmware
        )
        signaturService.ocmf(
            key = chargePoint.configuration.eichrechtKey,
            transactionId = transaction.idValue,
            idTag = transaction.idTag,
            startMeter = transaction.startMeter,
            endMeter = transaction.endMeter,
            startTime = transaction.startTime,
            endTime = transaction.endTime ?: Instant.now()
        )
    }

    try {
        ocppClientV16.asCoreProfile(sessionInfo).stopTransaction(
            StopTransactionRequest(
                idTag = transaction.idTag,
                meterStop = transaction.endMeter.roundToInt(),
                timestamp = ZonedDateTime.now(),
                transactionId = transaction.externalId,
                reason = reason,
                transactionData = listOf(
                    MeterValue(
                        timestamp = ZonedDateTime.now(),
                        sampledValue = listOf(
                            SampledValue(
                                value = ocmf,
                                context = "Transaction.End",
                                format = ValueFormat.SignedData.name,
                                measurand = "Energy.Active.Import.Register",
                                unit = "Wh"
                            )
                        )
                    )
                )
            )
        )
    } catch (exception: Exception) {
        logger.warn("Failed to stop charge", exception)
    }
}
