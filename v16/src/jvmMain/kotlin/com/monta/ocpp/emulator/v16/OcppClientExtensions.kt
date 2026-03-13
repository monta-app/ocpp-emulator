package com.monta.ocpp.emulator.v16

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
import com.monta.ocpp.emulator.chargepoint.model.MeterType
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.eichrecht.EichrechtSignatureService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

suspend fun statusNotification(
    sessionInfo: OcppSession.Info,
    connectorId: Int,
    status: ChargePointStatus,
    errorCode: ChargePointErrorCode,
    info: String? = null,
    timestamp: ZonedDateTime = ZonedDateTime.now(),
    vendorId: String? = null,
    vendorErrorCode: String? = null,
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
                vendorErrorCode = vendorErrorCode,
            ),
        )
    } catch (exception: Exception) {
        logger.warn(exception) { "Failed to send status notification" }
    }
}

suspend fun startTransaction(
    sessionInfo: OcppSession.Info,
    connector: ChargePointConnectorDAO,
    idTag: String,
): StartTransactionConfirmation {
    val ocppClientV16: OcppClientV16 by injectAnywhere()

    try {
        return ocppClientV16.asCoreProfile(sessionInfo).startTransaction(
            StartTransactionRequest(
                connectorId = connector.position,
                idTag = idTag,
                meterStart = 0,
                timestamp = ZonedDateTime.now(),
            ),
        )
    } catch (exception: Exception) {
        logger.warn(exception) { "Failed to stop charge" }
        throw exception
    }
}

suspend fun stopTransaction(
    sessionInfo: OcppSession.Info,
    transaction: ChargePointTransactionDAO,
    reason: Reason?,
) {
    val ocppClientV16: OcppClientV16 by injectAnywhere()

    val transactionData = transaction {
        val chargePoint = transaction.chargePoint
        when (chargePoint.meterType) {
            MeterType.Eichrecht -> {
                val signatureService = EichrechtSignatureService(
                    chargePointIdentity = chargePoint.identity,
                    brand = chargePoint.brand,
                    model = chargePoint.model,
                    serial = chargePoint.serial,
                    firmware = chargePoint.firmware,
                )
                val ocmf = signatureService.ocmf(
                    key = chargePoint.configuration.eichrechtKey,
                    transactionId = transaction.idValue,
                    idTag = transaction.idTag,
                    startMeter = transaction.startMeter,
                    endMeter = transaction.endMeter,
                    startTime = transaction.startTime,
                    endTime = transaction.endTime ?: Instant.now(),
                )
                listOf(
                    MeterValue(
                        timestamp = ZonedDateTime.now(),
                        sampledValue = listOf(
                            SampledValue(
                                value = ocmf,
                                context = "Transaction.End",
                                format = ValueFormat.SignedData.name,
                                measurand = "Energy.Active.Import.Register",
                                unit = "Wh",
                            ),
                        ),
                    ),
                )
            }
            MeterType.OcppHighPrecision -> {
                val startValue = transaction.startMeter + Random.nextDouble(0.0, 1.0)
                val endValue = transaction.endMeter + Random.nextDouble(0.0, 1.0)
                val startTime = transaction.startTime.atZone(ZoneOffset.UTC)
                val endTime = (transaction.endTime ?: Instant.now()).atZone(ZoneOffset.UTC)
                listOf(
                    MeterValue(
                        timestamp = startTime,
                        sampledValue = listOf(
                            SampledValue(
                                value = "%.1f".format(Locale.US, startValue),
                                context = "Transaction.Begin",
                                measurand = "Energy.Active.Import.Register",
                                location = "Outlet",
                                unit = "Wh",
                            ),
                        ),
                    ),
                    MeterValue(
                        timestamp = endTime,
                        sampledValue = listOf(
                            SampledValue(
                                value = "%.1f".format(Locale.US, endValue),
                                context = "Transaction.End",
                                measurand = "Energy.Active.Import.Register",
                                location = "Outlet",
                                unit = "Wh",
                            ),
                        ),
                    ),
                )
            }
            MeterType.OCPP -> null
        }
    }

    try {
        ocppClientV16.asCoreProfile(sessionInfo).stopTransaction(
            StopTransactionRequest(
                idTag = transaction.idTag,
                meterStop = transaction.endMeter.roundToInt(),
                timestamp = ZonedDateTime.now(),
                transactionId = transaction.externalId,
                reason = reason,
                transactionData = transactionData,
            ),
        )
    } catch (exception: Exception) {
        logger.warn(exception) { "Failed to stop charge" }
    }
}
