// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.security

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SecurityEventNotificationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SecurityEventNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SecurityEventNotificationResponse

val securityFeatures = listOf(
    SecurityEventNotificationFeature,
)

class SecurityClientDispatcher : ProfileDispatcher {
    override val featureList = securityFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
    }

    interface Sender {
        suspend fun securityEventNotification(
            request: SecurityEventNotificationRequest,
        ): SecurityEventNotificationResponse
    }
}
