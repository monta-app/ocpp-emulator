package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.displaymessage.DisplayMessageClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDisplayMessageRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDisplayMessageResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDisplayMessagesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDisplayMessagesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDisplayMessageRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDisplayMessageResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearMessageStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DisplayMessageStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetDisplayMessagesStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class DisplayMessageHandler : DisplayMessageClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun clearDisplayMessage(
        ocppSessionInfo: OcppSession.Info,
        request: ClearDisplayMessageRequest,
    ): ClearDisplayMessageResponse {
        stateStore.record("ClearDisplayMessage", request)
        return ClearDisplayMessageResponse(status = ClearMessageStatusEnum.Accepted)
    }

    override suspend fun getDisplayMessages(
        ocppSessionInfo: OcppSession.Info,
        request: GetDisplayMessagesRequest,
    ): GetDisplayMessagesResponse {
        stateStore.record("GetDisplayMessages", request)
        return GetDisplayMessagesResponse(status = GetDisplayMessagesStatusEnum.Accepted)
    }

    override suspend fun setDisplayMessage(
        ocppSessionInfo: OcppSession.Info,
        request: SetDisplayMessageRequest,
    ): SetDisplayMessageResponse {
        stateStore.record("SetDisplayMessage", request)
        return SetDisplayMessageResponse(status = DisplayMessageStatusEnum.Accepted)
    }
}
