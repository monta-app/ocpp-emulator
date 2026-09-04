package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.core.Reason
import com.monta.library.ocpp.v201.blocks.remotecontrol.RemoteControlClientDispatcher
import com.monta.library.ocpp.v201.blocks.remotecontrol.RequestStartTransactionRequest
import com.monta.library.ocpp.v201.blocks.remotecontrol.RequestStartTransactionResponse
import com.monta.library.ocpp.v201.blocks.remotecontrol.RequestStopTransactionRequest
import com.monta.library.ocpp.v201.blocks.remotecontrol.RequestStopTransactionResponse
import com.monta.library.ocpp.v201.blocks.remotecontrol.TriggerMessageRequest
import com.monta.library.ocpp.v201.blocks.remotecontrol.TriggerMessageResponse
import com.monta.library.ocpp.v201.blocks.remotecontrol.UnlockConnectorRequest
import com.monta.library.ocpp.v201.blocks.remotecontrol.UnlockConnectorResponse
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v201.service.ChargePointManager
import com.monta.ocpp.emulator.platform.util.launchThread
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import javax.inject.Singleton

@Singleton
class RemoteControlHandler(
    private val chargePointService: ChargePointService,
    private val chargePointManager: ChargePointManager,
) : RemoteControlClientDispatcher.Listener {
    override suspend fun requestStartTransaction(
        ocppSessionInfo: OcppSession.Info,
        request: RequestStartTransactionRequest,
    ): RequestStartTransactionResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val evseId = (request.evseId ?: 1L).toInt()
        val connector = chargePoint.getConnector(evseId)
        val idToken = request.idToken.idToken
        if (!chargePoint.canPerformAction) {
            return RequestStartTransactionResponse(status = RequestStartTransactionResponse.Status.Rejected)
        }
        val txId = UUID.randomUUID().toString().take(36)
        launchThread { chargePointManager.startTransaction(connector, idToken, txId) }
        return RequestStartTransactionResponse(
            status = RequestStartTransactionResponse.Status.Accepted,
            transactionId = txId,
        )
    }

    override suspend fun requestStopTransaction(
        ocppSessionInfo: OcppSession.Info,
        request: RequestStopTransactionRequest,
    ): RequestStopTransactionResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val active = chargePoint.getActiveTransactions().firstOrNull {
            it.ocppTransactionId == request.transactionId || it.externalId.toString() == request.transactionId
        }
        if (active == null) {
            return RequestStopTransactionResponse(status = RequestStopTransactionResponse.Status.Rejected)
        }
        launchThread { chargePointManager.stopTransaction(active, Reason.Remote) }
        return RequestStopTransactionResponse(status = RequestStopTransactionResponse.Status.Accepted)
    }

    override suspend fun triggerMessage(
        ocppSessionInfo: OcppSession.Info,
        request: TriggerMessageRequest,
    ): TriggerMessageResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        launchThread {
            chargePointManager.handleTrigger(
                chargePoint,
                request.requestedMessage.name,
                request.evse?.id?.toInt(),
            )
        }
        return TriggerMessageResponse(status = TriggerMessageResponse.Status.Accepted)
    }

    override suspend fun unlockConnector(
        ocppSessionInfo: OcppSession.Info,
        request: UnlockConnectorRequest,
    ): UnlockConnectorResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val connector = chargePoint.getConnector(request.evseId.toInt())
        transaction { connector.locked = false }
        return UnlockConnectorResponse(status = UnlockConnectorResponse.Status.Unlocked)
    }
}
