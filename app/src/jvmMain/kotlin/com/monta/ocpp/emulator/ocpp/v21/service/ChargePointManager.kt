package com.monta.ocpp.emulator.ocpp.v21.service

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.devicemodel.service.DeviceModelService
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.chargepoint.transaction.service.ChargePointTransactionService
import com.monta.ocpp.emulator.ocpp.v21.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v21.extension.setStatus21
import com.monta.ocpp.emulator.ocpp.v21.protocol.client.OcppClientV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AuthorizeRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BootNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.HeartbeatRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TransactionEventRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.AuthorizationStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.BootReasonEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingStation
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Component
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.EVSE
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdToken
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MeasurandEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MessageTriggerEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MeterValue
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MutabilityEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ReasonEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.RegistrationStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ReportData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SampledValue
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Transaction
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TransactionEventEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TriggerReasonEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.UnitOfMeasure
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Variable
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.VariableAttribute
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import kotlinx.coroutines.delay
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Singleton
class ChargePointManager {
    private val ocppClientV21: OcppClientV21 by injectAnywhere()
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
        val confirmation = ocppClientV21.asProvisioning(chargePoint.sessionInfo).bootNotification(
            BootNotificationRequest(
                chargingStation = ChargingStation(
                    model = chargePoint.model,
                    vendorName = chargePoint.brand,
                    serialNumber = chargePoint.serial,
                    firmwareVersion = chargePoint.firmware,
                ),
                reason = BootReasonEnum.PowerUp,
            ),
        )
        when (confirmation.status) {
            RegistrationStatusEnum.Accepted -> {
                GlobalLogger.info(chargePoint, "Boot was accepted")
                transaction { chargePoint.bootedAt = Instant.now() }
                chargePointService.update(chargePoint) {
                    updateConfiguration { heartbeatInterval = confirmation.interval.toLong() }
                    heartbeatAt = Instant.now()
                }
                chargePoint.setStatus21(ChargePointStatus.Available, ChargePointErrorCode.NoError)
                for (connector in chargePoint.getConnectors()) {
                    connector.setStatus21(ChargePointStatus.Available, ChargePointErrorCode.NoError, forceUpdate = true)
                }
            }
            RegistrationStatusEnum.Pending -> {
                GlobalLogger.info(chargePoint, "Boot pending, retrying in ${confirmation.interval}s")
                delay(confirmation.interval.toLong() * 1000)
                startBootSequence(chargePoint)
            }
            RegistrationStatusEnum.Rejected -> {
                GlobalLogger.info(chargePoint, "Boot rejected, reconnecting in ${confirmation.interval}s")
                connectionManager.reconnect(chargePoint.idValue, confirmation.interval)
            }
        }
    }

    suspend fun heartbeat(
        chargePoint: ChargePointDAO,
    ) {
        try {
            ocppClientV21.asAvailability(chargePoint.sessionInfo).heartbeat(HeartbeatRequest())
        } catch (exception: Exception) {
            GlobalLogger.warn(chargePoint, "Failed to send heartbeat")
        }
    }

    suspend fun handleTrigger(
        chargePoint: ChargePointDAO,
        requestedMessage: MessageTriggerEnum,
        evseId: Int?,
    ) {
        when (requestedMessage) {
            MessageTriggerEnum.BootNotification -> startBootSequence(chargePoint)
            MessageTriggerEnum.Heartbeat -> heartbeat(chargePoint)
            MessageTriggerEnum.StatusNotification -> {
                if (evseId == null) {
                    for (connector in chargePoint.getConnectors()) {
                        connector.setStatus21(connector.status, connector.errorCode, forceUpdate = true)
                    }
                } else {
                    val connector = chargePoint.getConnector(evseId)
                    connector.setStatus21(status = connector.status, forceUpdate = true)
                }
            }
            MessageTriggerEnum.MeterValues,
            MessageTriggerEnum.TransactionEvent,
            -> {
                chargePoint.getActiveTransactions().forEach { sendTransactionUpdate(it) }
            }
            else -> GlobalLogger.info(chargePoint, "Trigger $requestedMessage acknowledged")
        }
    }

    suspend fun sendBaseReport(
        chargePoint: ChargePointDAO,
        requestId: Int,
    ) {
        val variables = deviceModelService.getAll(chargePoint.idValue)
        val reportData = variables.map { variable ->
            ReportData(
                component = Component(
                    name = variable.componentName,
                    instance = variable.componentInstance,
                ),
                variable = Variable(
                    name = variable.variableName,
                    instance = variable.variableInstance,
                ),
                variableAttribute = listOf(
                    VariableAttribute(
                        value = variable.value,
                        mutability = if (variable.readonly) MutabilityEnum.ReadOnly else MutabilityEnum.ReadWrite,
                    ),
                ),
            )
        }
        ocppClientV21.asProvisioning(chargePoint.sessionInfo).notifyReport(
            NotifyReportRequest(
                generatedAt = ZonedDateTime.now(),
                requestId = requestId,
                seqNo = 0,
                tbc = false,
                reportData = reportData,
            ),
        )
    }

    suspend fun startTransaction(
        connector: ChargePointConnectorDAO,
        idTag: String,
    ) {
        val chargePoint = connector.getChargePoint()
        val txId = UUID.randomUUID().toString()
        val localTx = transactionService.create(
            chargePoint = chargePoint,
            chargePointConnector = connector,
            externalId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            idTag = idTag,
        )
        transaction {
            localTx.ocppTransactionId = txId
            connector.activeTransaction = localTx
            connector.status = ChargePointStatus.Charging
        }
        seqCounters.getOrPut(localTx.id.value) { AtomicInteger(0) }
        sendTransactionEvent(
            chargePoint = chargePoint,
            connector = connector,
            transactionId = txId,
            eventType = TransactionEventEnum.Started,
            triggerReason = TriggerReasonEnum.RemoteStart,
            idTag = idTag,
            meterValue = localTx.startMeter,
            localId = localTx.id.value,
        )
        connector.setStatus21(ChargePointStatus.Charging, forceUpdate = true)
    }

    suspend fun stopTransaction(
        transaction: ChargePointTransactionDAO,
        reason: Reason,
    ) {
        val connector = transaction.chargePointConnector
        val chargePoint = transaction.chargePoint
        val txId = transaction.ocppTransactionId ?: transaction.externalId.toString()
        sendTransactionEvent(
            chargePoint = chargePoint,
            connector = connector,
            transactionId = txId,
            eventType = TransactionEventEnum.Ended,
            triggerReason = TriggerReasonEnum.RemoteStop,
            idTag = transaction.idTag,
            meterValue = transaction.endMeter,
            localId = transaction.id.value,
            stoppedReason = reason.toOcpp21(),
        )
        transaction {
            transaction.endTime = Instant.now()
            transaction.endReason = reason
            connector.activeTransaction = null
        }
        connector.setStatus21(ChargePointStatus.Finishing, forceUpdate = true)
        delay(300)
        connector.setStatus21(ChargePointStatus.Available, forceUpdate = true)
    }

    suspend fun sendTransactionUpdate(
        transaction: ChargePointTransactionDAO,
    ) {
        val txId = transaction.ocppTransactionId ?: return
        sendTransactionEvent(
            chargePoint = transaction.chargePoint,
            connector = transaction.chargePointConnector,
            transactionId = txId,
            eventType = TransactionEventEnum.Updated,
            triggerReason = TriggerReasonEnum.MeterValuePeriodic,
            idTag = transaction.idTag,
            meterValue = transaction.endMeter,
            localId = transaction.id.value,
        )
    }

    private suspend fun sendTransactionEvent(
        chargePoint: ChargePointDAO,
        connector: ChargePointConnectorDAO,
        transactionId: String,
        eventType: TransactionEventEnum,
        triggerReason: TriggerReasonEnum,
        idTag: String,
        meterValue: Double,
        localId: Long,
        stoppedReason: ReasonEnum? = null,
    ) {
        val seq = seqCounters.getOrPut(localId) { AtomicInteger(0) }.getAndIncrement()
        ocppClientV21.asTransactions(chargePoint.sessionInfo).transactionEvent(
            TransactionEventRequest(
                eventType = eventType,
                seqNo = seq,
                timestamp = ZonedDateTime.now(),
                transactionInfo = Transaction(
                    transactionId = transactionId,
                    stoppedReason = stoppedReason,
                ),
                triggerReason = triggerReason,
                evse = EVSE(id = connector.position, connectorId = 1),
                idToken = IdToken(idToken = idTag, type = "Central"),
                meterValue = listOf(
                    MeterValue(
                        timestamp = ZonedDateTime.now(),
                        sampledValue = listOf(
                            SampledValue(
                                value = meterValue,
                                measurand = MeasurandEnum.EnergyActiveImportRegister,
                                unitOfMeasure = UnitOfMeasure(unit = "Wh"),
                            ),
                        ),
                    ),
                ),
                numberOfPhasesUsed = connector.vehicleNumberPhases,
            ),
        )
    }

    suspend fun authorize(
        chargePoint: ChargePointDAO,
        idTag: String,
    ): Boolean {
        val response = ocppClientV21.asAuthorization(chargePoint.sessionInfo).authorize(
            AuthorizeRequest(
                idToken = IdToken(idToken = idTag, type = "Central"),
            ),
        )
        return response.idTokenInfo.status == AuthorizationStatusEnum.Accepted
    }

    private fun Reason.toOcpp21(): ReasonEnum = when (this) {
        Reason.DeAuthorized -> ReasonEnum.DeAuthorized
        Reason.EmergencyStop -> ReasonEnum.EmergencyStop
        Reason.EVDisconnected -> ReasonEnum.EVDisconnected
        Reason.HardReset -> ReasonEnum.ImmediateReset
        Reason.Local -> ReasonEnum.Local
        Reason.Other -> ReasonEnum.Other
        Reason.PowerLoss -> ReasonEnum.PowerLoss
        Reason.Reboot -> ReasonEnum.Reboot
        Reason.Remote -> ReasonEnum.Remote
        Reason.SoftReset -> ReasonEnum.ImmediateReset
        Reason.UnlockCommand -> ReasonEnum.Other
    }
}
