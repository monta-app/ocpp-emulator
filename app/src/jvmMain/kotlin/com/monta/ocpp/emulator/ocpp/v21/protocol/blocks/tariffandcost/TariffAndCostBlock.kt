// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariffandcost

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CostUpdatedFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CostUpdatedRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CostUpdatedResponse

val tariffandcostFeatures = listOf(
    CostUpdatedFeature,
)

class TariffAndCostClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = tariffandcostFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is CostUpdatedRequest -> listener.costUpdated(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun costUpdated(
            ocppSessionInfo: OcppSession.Info,
            request: CostUpdatedRequest,
        ): CostUpdatedResponse
    }
}
