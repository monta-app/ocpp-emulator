// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.authorization

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AuthorizeFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AuthorizeRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AuthorizeResponse

val authorizationFeatures = listOf(
    AuthorizeFeature,
)

class AuthorizationClientDispatcher : ProfileDispatcher {
    override val featureList = authorizationFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
    }

    interface Sender {
        suspend fun authorize(
            request: AuthorizeRequest,
        ): AuthorizeResponse
    }
}
