package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.dynamicschedule.DynamicScheduleClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateDynamicScheduleRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateDynamicScheduleResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class DynamicScheduleHandler : DynamicScheduleClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun updateDynamicSchedule(
        ocppSessionInfo: OcppSession.Info,
        request: UpdateDynamicScheduleRequest,
    ): UpdateDynamicScheduleResponse {
        stateStore.record("UpdateDynamicSchedule", request)
        return UpdateDynamicScheduleResponse(status = ChargingProfileStatusEnum.Accepted)
    }
}
