// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.settlement

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifySettlementFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifySettlementRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifySettlementResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyWebPaymentStartedFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyWebPaymentStartedRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyWebPaymentStartedResponse

val settlementFeatures = listOf(
    NotifySettlementFeature,
    NotifyWebPaymentStartedFeature,
)

class SettlementClientDispatcher : ProfileDispatcher {
    override val featureList = settlementFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
    }

    interface Sender {
        suspend fun notifySettlement(
            request: NotifySettlementRequest,
        ): NotifySettlementResponse

        suspend fun notifyWebPaymentStarted(
            request: NotifyWebPaymentStartedRequest,
        ): NotifyWebPaymentStartedResponse
    }
}
