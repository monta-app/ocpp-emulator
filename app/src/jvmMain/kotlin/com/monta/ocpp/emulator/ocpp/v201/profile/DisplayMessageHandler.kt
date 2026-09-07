package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.displaymessage.ClearDisplayMessageRequest
import com.monta.library.ocpp.v201.blocks.displaymessage.ClearDisplayMessageResponse
import com.monta.library.ocpp.v201.blocks.displaymessage.DisplayMessageClientDispatcher
import com.monta.library.ocpp.v201.blocks.displaymessage.GetDisplayMessagesRequest
import com.monta.library.ocpp.v201.blocks.displaymessage.GetDisplayMessagesResponse
import com.monta.library.ocpp.v201.blocks.displaymessage.SetDisplayMessageRequest
import com.monta.library.ocpp.v201.blocks.displaymessage.SetDisplayMessageResponse
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.devicemodel.service.DisplayMessageService
import com.monta.ocpp.emulator.platform.database.extension.idValue
import javax.inject.Singleton

@Singleton
class DisplayMessageHandler(
    private val chargePointService: ChargePointService,
    private val displayMessageService: DisplayMessageService,
) : DisplayMessageClientDispatcher.Listener {
    override suspend fun clearDisplayMessage(
        ocppSessionInfo: OcppSession.Info,
        request: ClearDisplayMessageRequest,
    ): ClearDisplayMessageResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val cleared = displayMessageService.clear(chargePoint.idValue, request.id)
        return ClearDisplayMessageResponse(
            status = if (cleared) ClearDisplayMessageResponse.Status.Accepted else ClearDisplayMessageResponse.Status.Unknown,
        )
    }

    override suspend fun getDisplayMessages(
        ocppSessionInfo: OcppSession.Info,
        request: GetDisplayMessagesRequest,
    ): GetDisplayMessagesResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return GetDisplayMessagesResponse(status = GetDisplayMessagesResponse.Status.Accepted)
    }

    override suspend fun setDisplayMessage(
        ocppSessionInfo: OcppSession.Info,
        request: SetDisplayMessageRequest,
    ): SetDisplayMessageResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        displayMessageService.set(chargePoint.idValue, request.message.id, request.message)
        return SetDisplayMessageResponse(status = SetDisplayMessageResponse.Status.Accepted)
    }
}
