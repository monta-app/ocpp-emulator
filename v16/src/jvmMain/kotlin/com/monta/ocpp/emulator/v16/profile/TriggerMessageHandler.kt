package com.monta.ocpp.emulator.v16.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageClientProfile
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageConfirmation
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageRequest
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageRequestType
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageStatus
import com.monta.library.ocpp.v16.core.MeterValue
import com.monta.library.ocpp.v16.core.MeterValuesRequest
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.chargepointconnector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.ChargePointManager
import com.monta.ocpp.emulator.v16.setStatus
import com.monta.ocpp.emulator.v16.util.MeterValuesGenerator
import com.monta.ocpp.emulator.logger.GlobalLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton
import java.time.ZonedDateTime

@Singleton
class TriggerMessageHandler : TriggerMessageClientProfile.Listener {

    private val chargePointManager: ChargePointManager by injectAnywhere()
    private val chargePointService: ChargePointService by injectAnywhere()
    private val chargePointConnectorService: ChargePointConnectorService by injectAnywhere()
    private val ocppClientV16: OcppClientV16 by injectAnywhere()

    override suspend fun triggerMessage(
        ocppSessionInfo: OcppSession.Info,
        request: TriggerMessageRequest
    ): TriggerMessageConfirmation {
        try {
            return TriggerMessageConfirmation(
                status = TriggerMessageStatus.Accepted
            )
        } finally {
            launchThread {
                handleTriggerMessage(
                    ocppSessionInfo = ocppSessionInfo,
                    request = request
                )
            }
        }
    }

    private suspend fun handleTriggerMessage(
        ocppSessionInfo: OcppSession.Info,
        request: TriggerMessageRequest
    ) {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        when (request.requestedMessage) {
            TriggerMessageRequestType.BootNotification -> {
                chargePointManager.startBootSequence(chargePoint)
            }

            TriggerMessageRequestType.DiagnosticsStatusNotification -> {
                chargePointManager.diagnosticsStatusNotification(
                    chargePoint = chargePoint,
                    status = chargePoint.diagnosticsStatus
                )
            }

            TriggerMessageRequestType.FirmwareStatusNotification -> {
                chargePointManager.firmwareStatusNotification(
                    chargePoint = chargePoint,
                    status = chargePoint.firmwareStatus
                )
            }

            TriggerMessageRequestType.Heartbeat -> {
                chargePointManager.heartbeat(chargePoint)
            }

            TriggerMessageRequestType.MeterValues -> {
                triggerMeterValues(request, chargePoint)
            }

            TriggerMessageRequestType.StatusNotification -> {
                triggerStatusNotification(request, chargePoint)
            }
        }
    }

    private suspend fun triggerStatusNotification(
        request: TriggerMessageRequest,
        chargePoint: ChargePointDAO
    ) {
        val connectorId = request.connectorId ?: 0

        if (connectorId == 0) {
            chargePoint.setStatus(
                status = chargePoint.status,
                errorCode = chargePoint.errorCode
            )
        } else {
            val connector = chargePointConnectorService.get(
                chargePointId = chargePoint.idValue,
                connectorId = connectorId
            )
            if (connector == null) {
                return
            }
            connector.setStatus(
                status = connector.status,
                errorCode = connector.errorCode,
                vendorId = if (connector.vendorId.isNullOrBlank()) null else connector.vendorId,
                vendorErrorCode = if (connector.vendorErrorCode.isNullOrBlank()) null else connector.vendorErrorCode,
                info = if (connector.statusInfo.isNullOrBlank()) null else connector.statusInfo,
                forceUpdate = true
            )
        }
    }

    private suspend fun triggerMeterValues(
        request: TriggerMessageRequest,
        chargePoint: ChargePointDAO
    ) {
        val connectorId = request.connectorId
        
        if (connectorId != null) {
            // Send meter values for specific connector
            val connector = chargePointConnectorService.get(
                chargePointId = chargePoint.idValue,
                connectorId = connectorId
            )
            if (connector != null) {
                sendMeterValuesForConnector(chargePoint, connector)
            }
        } else {
            // Send meter values for all active transactions or all connectors
            val activeTransactions = transaction { chargePoint.getActiveTransactions() }
            
            if (activeTransactions.isNotEmpty()) {
                // Send meter values for active transactions
                for (transaction in activeTransactions) {
                    sendMeterValuesForTransaction(chargePoint, transaction)
                }
            } else {
                // No active transactions, send meter values for all connectors
                val connectors = chargePoint.getConnectors()
                for (connector in connectors) {
                    sendMeterValuesForConnector(chargePoint, connector)
                }
            }
        }
    }

    private suspend fun sendMeterValuesForTransaction(
        chargePoint: ChargePointDAO,
        transaction: com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
    ) {
        val connector = transaction { transaction.chargePointConnector }
        
        try {
            ocppClientV16.asCoreProfile(chargePoint.sessionInfo).meterValues(
                MeterValuesRequest(
                    connectorId = transaction.connectorPosition(),
                    transactionId = transaction.externalId,
                    meterValue = listOf(
                        MeterValue(
                            timestamp = ZonedDateTime.now(),
                            sampledValue = MeterValuesGenerator.generate(
                                meterValuesSampledData = chargePoint.configuration.meterValuesSampledData,
                                startTime = transaction.startTime,
                                endMeter = transaction.endMeter,
                                watts = connector.kw * 1000,
                                numberPhases = connector.vehicleNumberPhases
                            )
                        )
                    )
                )
            )
            
            GlobalLogger.info(transaction, "Meter values sent via trigger message")
        } catch (exception: Exception) {
            GlobalLogger.error(transaction, "Failed to send meter values via trigger message: ${exception.message}")
        }
    }

    private suspend fun sendMeterValuesForConnector(
        chargePoint: ChargePointDAO,
        connector: com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
    ) {
        try {
            ocppClientV16.asCoreProfile(chargePoint.sessionInfo).meterValues(
                MeterValuesRequest(
                    connectorId = connector.position,
                    transactionId = null, // No active transaction
                    meterValue = listOf(
                        MeterValue(
                            timestamp = ZonedDateTime.now(),
                            sampledValue = MeterValuesGenerator.generate(
                                meterValuesSampledData = chargePoint.configuration.meterValuesSampledData,
                                startTime = null, // No transaction start time
                                endMeter = connector.meterWh,
                                watts = connector.kw * 1000,
                                numberPhases = connector.vehicleNumberPhases
                            )
                        )
                    )
                )
            )
            
            GlobalLogger.info(connector, "Meter values sent via trigger message")
        } catch (exception: Exception) {
            GlobalLogger.error(connector, "Failed to send meter values via trigger message: ${exception.message}")
        }
    }
}
