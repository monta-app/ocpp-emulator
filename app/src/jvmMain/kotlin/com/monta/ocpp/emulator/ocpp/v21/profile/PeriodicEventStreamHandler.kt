package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.periodiceventstream.PeriodicEventStreamClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AdjustPeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AdjustPeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClosePeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClosePeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetPeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetPeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.OpenPeriodicEventStreamRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.OpenPeriodicEventStreamResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class PeriodicEventStreamHandler : PeriodicEventStreamClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun openPeriodicEventStream(
        ocppSessionInfo: OcppSession.Info,
        request: OpenPeriodicEventStreamRequest,
    ): OpenPeriodicEventStreamResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("OpenPeriodicEventStream", request)
        stateStore.streams(chargePoint.idValue)[request.constantStreamData.id] = request
        return OpenPeriodicEventStreamResponse(status = GenericStatusEnum.Accepted)
    }

    override suspend fun closePeriodicEventStream(
        ocppSessionInfo: OcppSession.Info,
        request: ClosePeriodicEventStreamRequest,
    ): ClosePeriodicEventStreamResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("ClosePeriodicEventStream", request)
        stateStore.streams(chargePoint.idValue).remove(request.id)
        return ClosePeriodicEventStreamResponse()
    }

    override suspend fun getPeriodicEventStream(
        ocppSessionInfo: OcppSession.Info,
        request: GetPeriodicEventStreamRequest,
    ): GetPeriodicEventStreamResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("GetPeriodicEventStream", request)
        return GetPeriodicEventStreamResponse()
    }

    override suspend fun adjustPeriodicEventStream(
        ocppSessionInfo: OcppSession.Info,
        request: AdjustPeriodicEventStreamRequest,
    ): AdjustPeriodicEventStreamResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("AdjustPeriodicEventStream", request)
        stateStore.streams(chargePoint.idValue)[request.id] = request
        return AdjustPeriodicEventStreamResponse(status = GenericStatusEnum.Accepted)
    }
}
