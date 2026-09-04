// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.remotecontrol

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStartTransactionFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStartTransactionRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStartTransactionResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStopTransactionFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStopTransactionRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestStopTransactionResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TriggerMessageFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TriggerMessageRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TriggerMessageResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnlockConnectorFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnlockConnectorRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnlockConnectorResponse

val remotecontrolFeatures = listOf(
    RequestStartTransactionFeature,
    RequestStopTransactionFeature,
    TriggerMessageFeature,
    UnlockConnectorFeature,
)

class RemoteControlClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = remotecontrolFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is RequestStartTransactionRequest -> listener.requestStartTransaction(ocppSessionInfo, request)
            is RequestStopTransactionRequest -> listener.requestStopTransaction(ocppSessionInfo, request)
            is TriggerMessageRequest -> listener.triggerMessage(ocppSessionInfo, request)
            is UnlockConnectorRequest -> listener.unlockConnector(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun requestStartTransaction(
            ocppSessionInfo: OcppSession.Info,
            request: RequestStartTransactionRequest,
        ): RequestStartTransactionResponse

        suspend fun requestStopTransaction(
            ocppSessionInfo: OcppSession.Info,
            request: RequestStopTransactionRequest,
        ): RequestStopTransactionResponse

        suspend fun triggerMessage(
            ocppSessionInfo: OcppSession.Info,
            request: TriggerMessageRequest,
        ): TriggerMessageResponse

        suspend fun unlockConnector(
            ocppSessionInfo: OcppSession.Info,
            request: UnlockConnectorRequest,
        ): UnlockConnectorResponse
    }
}
