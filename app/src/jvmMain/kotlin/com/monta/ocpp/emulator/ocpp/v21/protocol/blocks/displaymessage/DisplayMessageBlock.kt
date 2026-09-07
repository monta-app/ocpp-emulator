// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.displaymessage

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDisplayMessageFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDisplayMessageRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDisplayMessageResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDisplayMessagesFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDisplayMessagesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDisplayMessagesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDisplayMessagesFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDisplayMessagesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDisplayMessagesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDisplayMessageFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDisplayMessageRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDisplayMessageResponse

val displaymessageFeatures = listOf(
    ClearDisplayMessageFeature,
    GetDisplayMessagesFeature,
    SetDisplayMessageFeature,
    NotifyDisplayMessagesFeature,
)

class DisplayMessageClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = displaymessageFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is ClearDisplayMessageRequest -> listener.clearDisplayMessage(ocppSessionInfo, request)
            is GetDisplayMessagesRequest -> listener.getDisplayMessages(ocppSessionInfo, request)
            is SetDisplayMessageRequest -> listener.setDisplayMessage(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun clearDisplayMessage(
            ocppSessionInfo: OcppSession.Info,
            request: ClearDisplayMessageRequest,
        ): ClearDisplayMessageResponse

        suspend fun getDisplayMessages(
            ocppSessionInfo: OcppSession.Info,
            request: GetDisplayMessagesRequest,
        ): GetDisplayMessagesResponse

        suspend fun setDisplayMessage(
            ocppSessionInfo: OcppSession.Info,
            request: SetDisplayMessageRequest,
        ): SetDisplayMessageResponse
    }

    interface Sender {
        suspend fun notifyDisplayMessages(
            request: NotifyDisplayMessagesRequest,
        ): NotifyDisplayMessagesResponse
    }
}
