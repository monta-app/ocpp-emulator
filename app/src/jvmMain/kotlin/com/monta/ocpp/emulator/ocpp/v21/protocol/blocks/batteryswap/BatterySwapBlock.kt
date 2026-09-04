// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.batteryswap

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BatterySwapFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BatterySwapRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.BatterySwapResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestBatterySwapFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestBatterySwapRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestBatterySwapResponse

val batteryswapFeatures = listOf(
    RequestBatterySwapFeature,
    BatterySwapFeature,
)

class BatterySwapClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = batteryswapFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is RequestBatterySwapRequest -> listener.requestBatterySwap(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun requestBatterySwap(
            ocppSessionInfo: OcppSession.Info,
            request: RequestBatterySwapRequest,
        ): RequestBatterySwapResponse
    }

    interface Sender {
        suspend fun batterySwap(
            request: BatterySwapRequest,
        ): BatterySwapResponse
    }
}
