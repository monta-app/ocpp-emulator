// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.availability

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeAvailabilityFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeAvailabilityRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeAvailabilityResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.HeartbeatFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.HeartbeatRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.HeartbeatResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.StatusNotificationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.StatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.StatusNotificationResponse

val availabilityFeatures = listOf(
    HeartbeatFeature,
    StatusNotificationFeature,
    ChangeAvailabilityFeature,
)

class AvailabilityClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = availabilityFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is ChangeAvailabilityRequest -> listener.changeAvailability(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun changeAvailability(
            ocppSessionInfo: OcppSession.Info,
            request: ChangeAvailabilityRequest,
        ): ChangeAvailabilityResponse
    }

    interface Sender {
        suspend fun heartbeat(
            request: HeartbeatRequest,
        ): HeartbeatResponse

        suspend fun statusNotification(
            request: StatusNotificationRequest,
        ): StatusNotificationResponse
    }
}
