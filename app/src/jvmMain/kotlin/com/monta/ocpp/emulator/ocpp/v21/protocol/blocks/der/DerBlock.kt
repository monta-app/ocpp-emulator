// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.der

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDERControlFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDERControlRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDERControlResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDERControlFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDERControlRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDERControlResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERAlarmFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERAlarmRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERAlarmResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERStartStopFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERStartStopRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.NotifyDERStartStopResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportDERControlFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportDERControlRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ReportDERControlResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDERControlFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDERControlRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDERControlResponse

val derFeatures = listOf(
    SetDERControlFeature,
    GetDERControlFeature,
    ClearDERControlFeature,
    NotifyDERAlarmFeature,
    NotifyDERStartStopFeature,
    ReportDERControlFeature,
)

class DerClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = derFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is SetDERControlRequest -> listener.setDERControl(ocppSessionInfo, request)
            is GetDERControlRequest -> listener.getDERControl(ocppSessionInfo, request)
            is ClearDERControlRequest -> listener.clearDERControl(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun setDERControl(
            ocppSessionInfo: OcppSession.Info,
            request: SetDERControlRequest,
        ): SetDERControlResponse

        suspend fun getDERControl(
            ocppSessionInfo: OcppSession.Info,
            request: GetDERControlRequest,
        ): GetDERControlResponse

        suspend fun clearDERControl(
            ocppSessionInfo: OcppSession.Info,
            request: ClearDERControlRequest,
        ): ClearDERControlResponse
    }

    interface Sender {
        suspend fun notifyDERAlarm(
            request: NotifyDERAlarmRequest,
        ): NotifyDERAlarmResponse

        suspend fun notifyDERStartStop(
            request: NotifyDERStartStopRequest,
        ): NotifyDERStartStopResponse

        suspend fun reportDERControl(
            request: ReportDERControlRequest,
        ): ReportDERControlResponse
    }
}
