// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.prioritycharging

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPriorityChargingFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPriorityChargingRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyPriorityChargingResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UsePriorityChargingFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UsePriorityChargingRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UsePriorityChargingResponse

val prioritychargingFeatures = listOf(
    UsePriorityChargingFeature,
    NotifyPriorityChargingFeature,
)

class PriorityChargingClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = prioritychargingFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is UsePriorityChargingRequest -> listener.usePriorityCharging(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun usePriorityCharging(
            ocppSessionInfo: OcppSession.Info,
            request: UsePriorityChargingRequest,
        ): UsePriorityChargingResponse
    }

    interface Sender {
        suspend fun notifyPriorityCharging(
            request: NotifyPriorityChargingRequest,
        ): NotifyPriorityChargingResponse
    }
}
