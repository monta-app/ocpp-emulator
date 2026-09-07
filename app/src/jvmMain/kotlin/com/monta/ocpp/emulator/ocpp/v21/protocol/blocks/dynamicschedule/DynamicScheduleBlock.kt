// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.dynamicschedule

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PullDynamicScheduleUpdateFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PullDynamicScheduleUpdateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PullDynamicScheduleUpdateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateDynamicScheduleFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateDynamicScheduleRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateDynamicScheduleResponse

val dynamicscheduleFeatures = listOf(
    UpdateDynamicScheduleFeature,
    PullDynamicScheduleUpdateFeature,
)

class DynamicScheduleClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = dynamicscheduleFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is UpdateDynamicScheduleRequest -> listener.updateDynamicSchedule(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun updateDynamicSchedule(
            ocppSessionInfo: OcppSession.Info,
            request: UpdateDynamicScheduleRequest,
        ): UpdateDynamicScheduleResponse
    }

    interface Sender {
        suspend fun pullDynamicScheduleUpdate(
            request: PullDynamicScheduleUpdateRequest,
        ): PullDynamicScheduleUpdateResponse
    }
}
