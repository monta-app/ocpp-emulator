package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.prioritycharging.PriorityChargingClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UsePriorityChargingRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UsePriorityChargingResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.PriorityChargingStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class PriorityChargingHandler : PriorityChargingClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun usePriorityCharging(
        ocppSessionInfo: OcppSession.Info,
        request: UsePriorityChargingRequest,
    ): UsePriorityChargingResponse {
        stateStore.record("UsePriorityCharging", request)
        return UsePriorityChargingResponse(status = PriorityChargingStatusEnum.Accepted)
    }
}
