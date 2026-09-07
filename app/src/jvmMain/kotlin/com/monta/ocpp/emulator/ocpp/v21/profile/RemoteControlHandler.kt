package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.remotecontrol.RemoteControlClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStartTransactionRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStartTransactionResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStopTransactionRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStopTransactionResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TriggerMessageRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TriggerMessageResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnlockConnectorRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnlockConnectorResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.RequestStartStopStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TriggerMessageStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.UnlockStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.ChargePointManager
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.platform.util.launchThread
import javax.inject.Singleton

@Singleton
class RemoteControlHandler : RemoteControlClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val chargePointManager: ChargePointManager by injectAnywhere()
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun requestStartTransaction(
        ocppSessionInfo: OcppSession.Info,
        request: RequestStartTransactionRequest,
    ): RequestStartTransactionResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("RequestStartTransaction", request)
        if (!chargePoint.canPerformAction) {
            return RequestStartTransactionResponse(status = RequestStartStopStatusEnum.Rejected)
        }
        val connector = when (val evseId = request.evseId) {
            null -> chargePoint.getConnectors().firstOrNull { !it.hasActiveTransaction }
            else -> chargePoint.getConnector(evseId)
        }
        if (connector == null || connector.hasActiveTransaction) {
            return RequestStartTransactionResponse(status = RequestStartStopStatusEnum.Rejected)
        }
        launchThread {
            chargePointManager.startTransaction(connector, request.idToken.idToken)
        }
        return RequestStartTransactionResponse(status = RequestStartStopStatusEnum.Accepted)
    }

    override suspend fun requestStopTransaction(
        ocppSessionInfo: OcppSession.Info,
        request: RequestStopTransactionRequest,
    ): RequestStopTransactionResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("RequestStopTransaction", request)
        val transaction = chargePoint.getActiveTransactions().firstOrNull {
            it.ocppTransactionId == request.transactionId || it.externalId.toString() == request.transactionId
        }
        if (transaction == null) {
            return RequestStopTransactionResponse(status = RequestStartStopStatusEnum.Rejected)
        }
        launchThread {
            chargePointManager.stopTransaction(transaction, Reason.Remote)
        }
        return RequestStopTransactionResponse(status = RequestStartStopStatusEnum.Accepted)
    }

    override suspend fun triggerMessage(
        ocppSessionInfo: OcppSession.Info,
        request: TriggerMessageRequest,
    ): TriggerMessageResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("TriggerMessage", request)
        launchThread {
            chargePointManager.handleTrigger(
                chargePoint = chargePoint,
                requestedMessage = request.requestedMessage,
                evseId = request.evse?.id,
            )
        }
        return TriggerMessageResponse(status = TriggerMessageStatusEnum.Accepted)
    }

    override suspend fun unlockConnector(
        ocppSessionInfo: OcppSession.Info,
        request: UnlockConnectorRequest,
    ): UnlockConnectorResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("UnlockConnector", request)
        val connector = chargePoint.getConnector(request.evseId)
        if (connector.hasActiveTransaction) {
            return UnlockConnectorResponse(status = UnlockStatusEnum.OngoingAuthorizedTransaction)
        }
        return UnlockConnectorResponse(status = UnlockStatusEnum.Unlocked)
    }
}
