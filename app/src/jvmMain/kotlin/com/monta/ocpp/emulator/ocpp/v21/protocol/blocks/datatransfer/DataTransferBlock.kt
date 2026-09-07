// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.datatransfer

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DataTransferFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DataTransferRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DataTransferResponse

val datatransferFeatures = listOf(
    DataTransferFeature,
)

class DataTransferClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = datatransferFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is DataTransferRequest -> listener.dataTransfer(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun dataTransfer(
            ocppSessionInfo: OcppSession.Info,
            request: DataTransferRequest,
        ): DataTransferResponse
    }

    interface Sender {
        suspend fun dataTransfer(
            request: DataTransferRequest,
        ): DataTransferResponse
    }
}
