package com.monta.ocpp.emulator.ocpp.v201.service

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.library.ocpp.v201.blocks.authorization.AuthorizeRequest
import com.monta.library.ocpp.v201.blocks.availability.HeartbeatRequest
import com.monta.library.ocpp.v201.blocks.provisioning.BootNotificationRequest
import com.monta.library.ocpp.v201.blocks.provisioning.BootNotificationResponse
import com.monta.library.ocpp.v201.blocks.provisioning.NotifyReportRequest
import com.monta.library.ocpp.v201.blocks.transactions.TransactionEventRequest
import com.monta.library.ocpp.v201.client.OcppClientV201
import com.monta.library.ocpp.v201.common.AuthorizationStatus
import com.monta.library.ocpp.v201.common.Component
import com.monta.library.ocpp.v201.common.EVSE
import com.monta.library.ocpp.v201.common.IdToken
import com.monta.library.ocpp.v201.common.Measurand
import com.monta.library.ocpp.v201.common.MeterValue
import com.monta.library.ocpp.v201.common.SampledValue
import com.monta.library.ocpp.v201.common.Variable
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.devicemodel.service.DeviceModelService
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.chargepoint.transaction.service.ChargePointTransactionService
import com.monta.ocpp.emulator.ocpp.v201.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v201.extension.setStatus201
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import kotlinx.coroutines.delay
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.math.BigDecimal
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Singleton
class ChargePointManager {
    private val ocppClientV201: OcppClientV201 by injectAnywhere()
    private val chargePointService: ChargePointService by injectAnywhere()
    private val connectionManager: ConnectionManager by injectAnywhere()
    private val transactionService: ChargePointTransactionService by injectAnywhere()
    private val deviceModelService: DeviceModelService by injectAnywhere()
    private val seqCounters = mutableMapOf<Long, AtomicInteger>()

    suspend fun startBootSequence(
        chargePoint: ChargePointDAO,
    ) {
        deviceModelService.seedDefaults(
            chargePointId = chargePoint.idValue,
            vendor = chargePoint.brand,
            model = chargePoint.model,
            serial = chargePoint.serial,
            firmware = chargePoint.firmware,
        )
        val confirmation = ocppClientV201.asProvisioning(chargePoint.sessionInfo).bootNotification(
            BootNotificationRequest(
                chargingStation = BootNotificationRequest.ChargingStation(
                    model = chargePoint.model.take(20),
                    vendorName = chargePoint.brand.take(50),
                    serialNumber = chargePoint.serial.take(25),
                    firmwareVersion = chargePoint.firmware.take(50),
                ),
                reason = BootNotificationRequest.Reason.PowerUp,
            ),
        )
        when (confirmation.status) {
            BootNotificationResponse.Status.Accepted -> {
                GlobalLogger.info(chargePoint, "Boot was accepted")
                transaction { chargePoint.bootedAt = Instant.now() }
                chargePointService.update(chargePoint) {
                    updateConfiguration { heartbeatInterval = confirmation.interval }
                    heartbeatAt = Instant.now()
                }
                chargePoint.setStatus201(ChargePointStatus.Available, ChargePointErrorCode.NoError)
                for (connector in chargePoint.getConnectors()) {
                    connector.setStatus201(ChargePointStatus.Available, ChargePointErrorCode.NoError, forceUpdate = true)
                }
            }
            BootNotificationResponse.Status.Pending -> {
                GlobalLogger.info(chargePoint, "Boot pending, retrying in ${confirmation.interval}s")
                delay(confirmation.interval * 1000)
                startBootSequence(chargePoint)
            }
            BootNotificationResponse.Status.Rejected -> {
                GlobalLogger.info(chargePoint, "Boot rejected, reconnecting in ${confirmation.interval}s")
                connectionManager.reconnect(chargePoint.idValue, confirmation.interval.toInt())
            }
        }
    }

    suspend fun heartbeat(
        chargePoint: ChargePointDAO,
    ) {
        try {
            ocppClientV201.asAvailability(chargePoint.sessionInfo).heartbeat(HeartbeatRequest())
        } catch (_: Exception) {
            GlobalLogger.warn(chargePoint, "Failed to send heartbeat")
        }
    }

    suspend fun handleTrigger(
        chargePoint: ChargePointDAO,
        requestedMessage: String,
        evseId: Int?,
    ) {
        when (requestedMessage) {
            "BootNotification" -> startBootSequence(chargePoint)
            "Heartbeat" -> heartbeat(chargePoint)
            "StatusNotification" -> {
                if (evseId == null) {
                    chargePoint.getConnectors().forEach { it.setStatus201(it.status, it.errorCode, true) }
                } else {
                    val connector = chargePoint.getConnector(evseId)
                    connector.setStatus201(connector.status, forceUpdate = true)
                }
            }
            "MeterValues", "TransactionEvent" -> {
                chargePoint.getActiveTransactions().forEach { sendTransactionUpdate(it) }
            }
            else -> GlobalLogger.info(chargePoint, "Trigger $requestedMessage acknowledged")
        }
    }

    suspend fun sendBaseReport(
        chargePoint: ChargePointDAO,
        requestId: Long,
    ) {
        val reportData = deviceModelService.getAll(chargePoint.idValue).map { variable ->
            NotifyReportRequest.ReportData(
                component = Component(name = variable.componentName, instance = variable.componentInstance),
                variable = Variable(name = variable.variableName, instance = variable.variableInstance),
                variableAttribute = listOf(
                    NotifyReportRequest.VariableAttribute(
                        value = variable.value,
                        mutability = if (variable.readonly) {
                            NotifyReportRequest.Mutability.ReadOnly
                        } else {
                            NotifyReportRequest.Mutability.ReadWrite
                        },
                    ),
                ),
            )
        }
        ocppClientV201.asProvisioning(chargePoint.sessionInfo).notifyReport(
            NotifyReportRequest(
                requestId = requestId,
                generatedAt = ZonedDateTime.now(),
                seqNo = 0,
                reportData = reportData.ifEmpty { null },
                tbc = false,
            ),
        )
    }

    suspend fun startTransaction(
        connector: ChargePointConnectorDAO,
        idTag: String,
        transactionId: String = UUID.randomUUID().toString().take(36),
    ) {
        val chargePoint = connector.getChargePoint()
        val localTx = transactionService.create(
            chargePoint = chargePoint,
            chargePointConnector = connector,
            externalId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            idTag = idTag,
        )
        transaction {
            localTx.ocppTransactionId = transactionId
            connector.activeTransaction = localTx
            connector.status = ChargePointStatus.Charging
        }
        seqCounters.getOrPut(localTx.id.value) { AtomicInteger(0) }
        sendTransactionEvent(
            chargePoint = chargePoint,
            connector = connector,
            transactionId = transactionId,
            eventType = TransactionEventRequest.EventType.Started,
            triggerReason = TransactionEventRequest.TriggerReason.RemoteStart,
            idTag = idTag,
            meterValue = localTx.startMeter,
            localId = localTx.id.value,
        )
        connector.setStatus201(ChargePointStatus.Charging, forceUpdate = true)
    }

    suspend fun stopTransaction(
        transactionDao: ChargePointTransactionDAO,
        reason: Reason,
    ) {
        val connector = transactionDao.chargePointConnector
        val chargePoint = transactionDao.chargePoint
        val txId = transactionDao.ocppTransactionId ?: transactionDao.externalId.toString()
        sendTransactionEvent(
            chargePoint = chargePoint,
            connector = connector,
            transactionId = txId.take(36),
            eventType = TransactionEventRequest.EventType.Ended,
            triggerReason = TransactionEventRequest.TriggerReason.RemoteStop,
            idTag = transactionDao.idTag,
            meterValue = transactionDao.endMeter,
            localId = transactionDao.id.value,
            stoppedReason = TransactionEventRequest.Transaction.StoppedReason.Remote,
        )
        transaction {
            transactionDao.endTime = Instant.now()
            transactionDao.endReason = reason
            connector.activeTransaction = null
        }
        connector.setStatus201(ChargePointStatus.Finishing, forceUpdate = true)
        delay(300)
        connector.setStatus201(ChargePointStatus.Available, forceUpdate = true)
    }

    suspend fun sendTransactionUpdate(
        transactionDao: ChargePointTransactionDAO,
    ) {
        val txId = transactionDao.ocppTransactionId ?: return
        sendTransactionEvent(
            chargePoint = transactionDao.chargePoint,
            connector = transactionDao.chargePointConnector,
            transactionId = txId,
            eventType = TransactionEventRequest.EventType.Updated,
            triggerReason = TransactionEventRequest.TriggerReason.MeterValuePeriodic,
            idTag = transactionDao.idTag,
            meterValue = transactionDao.endMeter,
            localId = transactionDao.id.value,
        )
    }

    private suspend fun sendTransactionEvent(
        chargePoint: ChargePointDAO,
        connector: ChargePointConnectorDAO,
        transactionId: String,
        eventType: TransactionEventRequest.EventType,
        triggerReason: TransactionEventRequest.TriggerReason,
        idTag: String,
        meterValue: Double,
        localId: Long,
        stoppedReason: TransactionEventRequest.Transaction.StoppedReason? = null,
    ) {
        val seq = seqCounters.getOrPut(localId) { AtomicInteger(0) }.getAndIncrement().toLong()
        ocppClientV201.asTransactions(chargePoint.sessionInfo).transactionEvent(
            TransactionEventRequest(
                eventType = eventType,
                timestamp = ZonedDateTime.now(),
                triggerReason = triggerReason,
                seqNo = seq,
                transactionInfo = TransactionEventRequest.Transaction(
                    transactionId = transactionId.take(36),
                    stoppedReason = stoppedReason,
                ),
                meterValue = listOf(
                    MeterValue(
                        timestamp = ZonedDateTime.now(),
                        sampledValue = listOf(
                            SampledValue(
                                value = BigDecimal.valueOf(meterValue),
                                measurand = Measurand.EnergyActiveImportRegister,
                                unitOfMeasure = SampledValue.UnitOfMeasure(unit = "Wh"),
                            ),
                        ),
                    ),
                ),
                numberOfPhasesUsed = connector.vehicleNumberPhases.toLong(),
                evse = EVSE(id = connector.position.toLong(), connectorId = 1L),
                idToken = IdToken(idToken = idTag.take(36), type = IdToken.Type.Central),
            ),
        )
    }

    suspend fun authorize(
        chargePoint: ChargePointDAO,
        idTag: String,
    ): Boolean {
        val response = ocppClientV201.asAuthorization(chargePoint.sessionInfo).authorize(
            AuthorizeRequest(idToken = IdToken(idToken = idTag.take(36), type = IdToken.Type.Central)),
        )
        return response.idTokenInfo.status == AuthorizationStatus.Accepted
    }
}
