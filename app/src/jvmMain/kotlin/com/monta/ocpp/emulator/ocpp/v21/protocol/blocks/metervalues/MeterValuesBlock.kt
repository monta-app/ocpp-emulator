// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.metervalues

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.MeterValuesFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.MeterValuesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.MeterValuesResponse

val metervaluesFeatures = listOf(
    MeterValuesFeature,
)

class MeterValuesClientDispatcher : ProfileDispatcher {
    override val featureList = metervaluesFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
    }

    interface Sender {
        suspend fun meterValues(
            request: MeterValuesRequest,
        ): MeterValuesResponse
    }
}
