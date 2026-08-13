// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.afrr

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AFRRSignalFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AFRRSignalRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AFRRSignalResponse

val afrrFeatures = listOf(
    AFRRSignalFeature,
)

class AfrrClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = afrrFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is AFRRSignalRequest -> listener.aFRRSignal(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun aFRRSignal(
            ocppSessionInfo: OcppSession.Info,
            request: AFRRSignalRequest,
        ): AFRRSignalResponse
    }
}
