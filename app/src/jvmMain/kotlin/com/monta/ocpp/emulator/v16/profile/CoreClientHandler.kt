package com.monta.ocpp.emulator.v16.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.AuthorizationStatus
import com.monta.library.ocpp.v16.core.AvailabilityStatus
import com.monta.library.ocpp.v16.core.AvailabilityType
import com.monta.library.ocpp.v16.core.ChangeAvailabilityConfirmation
import com.monta.library.ocpp.v16.core.ChangeAvailabilityRequest
import com.monta.library.ocpp.v16.core.ChangeConfigurationConfirmation
import com.monta.library.ocpp.v16.core.ChangeConfigurationRequest
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.ClearCacheConfirmation
import com.monta.library.ocpp.v16.core.ClearCacheRequest
import com.monta.library.ocpp.v16.core.ClearCacheStatus
import com.monta.library.ocpp.v16.core.CoreClientProfile
import com.monta.library.ocpp.v16.core.DataTransferConfirmation
import com.monta.library.ocpp.v16.core.DataTransferRequest
import com.monta.library.ocpp.v16.core.DataTransferStatus
import com.monta.library.ocpp.v16.core.GetConfigurationConfirmation
import com.monta.library.ocpp.v16.core.GetConfigurationRequest
import com.monta.library.ocpp.v16.core.KeyValueType
import com.monta.library.ocpp.v16.core.Reason
import com.monta.library.ocpp.v16.core.RemoteStartStopStatus
import com.monta.library.ocpp.v16.core.RemoteStartTransactionConfirmation
import com.monta.library.ocpp.v16.core.RemoteStartTransactionRequest
import com.monta.library.ocpp.v16.core.RemoteStopTransactionConfirmation
import com.monta.library.ocpp.v16.core.RemoteStopTransactionRequest
import com.monta.library.ocpp.v16.core.ResetConfirmation
import com.monta.library.ocpp.v16.core.ResetRequest
import com.monta.library.ocpp.v16.core.ResetStatus
import com.monta.library.ocpp.v16.core.UnlockConnectorConfirmation
import com.monta.library.ocpp.v16.core.UnlockConnectorRequest
import com.monta.library.ocpp.v16.core.UnlockStatus
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.chargepointconnector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.chargepointtransaction.service.ChargePointTransactionService
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.logger.GlobalLogger
import com.monta.ocpp.emulator.v16.ChargePointManager
import com.monta.ocpp.emulator.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.v16.profile.configuration.ChangeConfigurationService
import com.monta.ocpp.emulator.v16.setStatus
import com.monta.ocpp.emulator.v16.stop
import com.monta.ocpp.emulator.v16.stopActiveTransactions
import kotlinx.coroutines.delay
import javax.inject.Singleton

@Singleton
class CoreClientHandler : CoreClientProfile.Listener {

    private val chargePointManager: ChargePointManager by injectAnywhere()
    private val connectionManager: ConnectionManager by injectAnywhere()
    private val chargePointService: ChargePointService by injectAnywhere()
    private val chargePointConnectorService: ChargePointConnectorService by injectAnywhere()
    private val chargePointTransactionService: ChargePointTransactionService by injectAnywhere()
    private val changeConfigurationService: ChangeConfigurationService by injectAnywhere()

    override suspend fun changeAvailabilityRequest(
        ocppSessionInfo: OcppSession.Info,
        request: ChangeAvailabilityRequest,
    ): ChangeAvailabilityConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        if (!chargePoint.canPerformAction) {
            return ChangeAvailabilityConfirmation(
                status = AvailabilityStatus.Rejected,
            )
        }

        launchThread {
            when (val connectorId = request.connectorId) {
                0 -> {
                    chargePoint.setStatus(
                        status = when (request.type) {
                            AvailabilityType.Inoperative -> ChargePointStatus.Unavailable
                            AvailabilityType.Operative -> ChargePointStatus.Available
                        },
                    )
                }

                else -> {
                    chargePoint.getConnector(connectorId).setStatus(
                        status = when (request.type) {
                            AvailabilityType.Inoperative -> ChargePointStatus.Unavailable
                            AvailabilityType.Operative -> ChargePointStatus.Available
                        },
                    )
                }
            }
        }

        return ChangeAvailabilityConfirmation(
            status = AvailabilityStatus.Accepted,
        )
    }

    override suspend fun getConfigurationRequest(
        ocppSessionInfo: OcppSession.Info,
        request: GetConfigurationRequest,
    ): GetConfigurationConfirmation {
        val unknownKeys = mutableListOf<String>()
        val keyValueTypes = mutableListOf<KeyValueType>()

        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        for (config in chargePoint.configuration) {
            if (request.key == null || request.key?.isEmpty() == true || request.key?.contains(config.key) == true) {
                keyValueTypes.add(
                    KeyValueType(
                        key = config.key,
                        readonly = false,
                        value = config.value,
                    ),
                )
            }
        }
        for (key in request.key ?: listOf()) {
            if (key !in chargePoint.configuration) {
                unknownKeys.add(key)
            }
        }

        return GetConfigurationConfirmation(
            configurationKey = keyValueTypes,
            unknownKey = unknownKeys,
        )
    }

    override suspend fun changeConfigurationRequest(
        ocppSessionInfo: OcppSession.Info,
        request: ChangeConfigurationRequest,
    ): ChangeConfigurationConfirmation {
        return changeConfigurationService.changeConfiguration(
            chargePointIdentity = ocppSessionInfo.identity,
            key = request.key,
            value = request.value,
        )
    }

    override suspend fun clearCacheRequest(
        ocppSessionInfo: OcppSession.Info,
        request: ClearCacheRequest,
    ): ClearCacheConfirmation {
        return ClearCacheConfirmation(
            status = ClearCacheStatus.Rejected,
        )
    }

    override suspend fun dataTransferRequest(
        ocppSessionInfo: OcppSession.Info,
        request: DataTransferRequest,
    ): DataTransferConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        var handled = false
        chargePointService.update(chargePoint) {
            handled = chargePoint.handleDataTransferRequest(request)
        }

        return DataTransferConfirmation(
            status = if (handled) DataTransferStatus.Accepted else DataTransferStatus.Rejected,
            data = null,
        )
    }

    override suspend fun remoteStartTransactionRequest(
        ocppSessionInfo: OcppSession.Info,
        request: RemoteStartTransactionRequest,
    ): RemoteStartTransactionConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        if (!chargePoint.canPerformAction) {
            return RemoteStartTransactionConfirmation(
                status = RemoteStartStopStatus.Rejected,
            )
        }

        val connectorId = request.connectorId

        if (connectorId == null) {
            GlobalLogger.warn(chargePoint, "remoteStartTransactionRequest missing connector id")
            return RemoteStartTransactionConfirmation(
                status = RemoteStartStopStatus.Rejected,
            )
        }

        val connector = chargePointConnectorService.get(
            chargePointId = chargePoint.idValue,
            connectorId = connectorId,
        )

        if (connector == null) {
            GlobalLogger.warn(chargePoint, "remoteStartTransactionRequest invalid connector position")
            return RemoteStartTransactionConfirmation(
                status = RemoteStartStopStatus.Rejected,
            )
        }

        try {
            return RemoteStartTransactionConfirmation(
                status = RemoteStartStopStatus.Accepted,
            )
        } finally {
            launchThread {
                val authorizationStatus = chargePointManager.authorize(
                    connector = connector,
                    idTag = request.idTag,
                )

                // If we don't get an accepted back from the authorization then return
                if (authorizationStatus != AuthorizationStatus.Accepted) {
                    return@launchThread
                }
            }
        }
    }

    override suspend fun remoteStopTransactionRequest(
        ocppSessionInfo: OcppSession.Info,
        request: RemoteStopTransactionRequest,
    ): RemoteStopTransactionConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        val chargePointTransaction = chargePointTransactionService.getByExternalId(
            externalId = request.transactionId,
        )

        if (chargePointTransaction == null) {
            GlobalLogger.error(chargePoint, "transaction not found for externalId ${request.transactionId}")
            return RemoteStopTransactionConfirmation(
                status = RemoteStartStopStatus.Rejected,
            )
        }

        if (!chargePointTransaction.isOwner(chargePoint)) {
            GlobalLogger.error(chargePointTransaction, "charge point doesn't own this transaction")
            return RemoteStopTransactionConfirmation(
                status = RemoteStartStopStatus.Rejected,
            )
        }

        if (!chargePointTransaction.canStop()) {
            GlobalLogger.error(chargePointTransaction, "charge already ended, rejecting")
            return RemoteStopTransactionConfirmation(
                status = RemoteStartStopStatus.Rejected,
            )
        }

        launchThread {
            delay(100)
            chargePointTransaction.stop(
                reason = Reason.Remote,
            )
        }

        return RemoteStopTransactionConfirmation(
            status = RemoteStartStopStatus.Accepted,
        )
    }

    override suspend fun resetRequest(
        ocppSessionInfo: OcppSession.Info,
        request: ResetRequest,
    ): ResetConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        GlobalLogger.info(chargePoint, "Reset requested")

        if (!chargePoint.canPerformAction) {
            return ResetConfirmation(
                status = ResetStatus.Rejected,
            )
        }

        launchThread {
            connectionManager.reconnect(
                chargePointId = chargePoint.idValue,
                delayInSeconds = 5,
            )
        }

        return ResetConfirmation(
            status = ResetStatus.Accepted,
        )
    }

    override suspend fun unlockConnectorRequest(
        ocppSessionInfo: OcppSession.Info,
        request: UnlockConnectorRequest,
    ): UnlockConnectorConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        return if (request.connectorId == 0) {
            GlobalLogger.warn(chargePoint, "Tried to unlock connector 0, not possible")

            UnlockConnectorConfirmation(
                status = UnlockStatus.NotSupported,
            )
        } else {
            val connector = chargePoint.getConnector(request.connectorId)

            GlobalLogger.info(connector, "Unlocking connector")

            chargePointConnectorService.update(connector) {
                this.locked = false
            }

            connector.stopActiveTransactions(
                reason = Reason.UnlockCommand,
                endReasonDescription = "Connector was unlocked",
            )

            UnlockConnectorConfirmation(
                status = UnlockStatus.Unlocked,
            )
        }
    }
}
