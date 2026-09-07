// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.periodiceventstream

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AdjustPeriodicEventStreamFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AdjustPeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AdjustPeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClosePeriodicEventStreamFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClosePeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClosePeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetPeriodicEventStreamFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetPeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetPeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPeriodicEventStreamFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.OpenPeriodicEventStreamFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.OpenPeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.OpenPeriodicEventStreamResponse

val periodiceventstreamFeatures = listOf(
    OpenPeriodicEventStreamFeature,
    ClosePeriodicEventStreamFeature,
    GetPeriodicEventStreamFeature,
    AdjustPeriodicEventStreamFeature,
    NotifyPeriodicEventStreamFeature,
)

class PeriodicEventStreamClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = periodiceventstreamFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is OpenPeriodicEventStreamRequest -> listener.openPeriodicEventStream(ocppSessionInfo, request)
            is ClosePeriodicEventStreamRequest -> listener.closePeriodicEventStream(ocppSessionInfo, request)
            is GetPeriodicEventStreamRequest -> listener.getPeriodicEventStream(ocppSessionInfo, request)
            is AdjustPeriodicEventStreamRequest -> listener.adjustPeriodicEventStream(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun openPeriodicEventStream(
            ocppSessionInfo: OcppSession.Info,
            request: OpenPeriodicEventStreamRequest,
        ): OpenPeriodicEventStreamResponse

        suspend fun closePeriodicEventStream(
            ocppSessionInfo: OcppSession.Info,
            request: ClosePeriodicEventStreamRequest,
        ): ClosePeriodicEventStreamResponse

        suspend fun getPeriodicEventStream(
            ocppSessionInfo: OcppSession.Info,
            request: GetPeriodicEventStreamRequest,
        ): GetPeriodicEventStreamResponse

        suspend fun adjustPeriodicEventStream(
            ocppSessionInfo: OcppSession.Info,
            request: AdjustPeriodicEventStreamRequest,
        ): AdjustPeriodicEventStreamResponse
    }

    interface Sender {
        suspend fun notifyPeriodicEventStream(
            request: NotifyPeriodicEventStreamRequest,
        ): NotifyPeriodicEventStreamResponse
    }
}
