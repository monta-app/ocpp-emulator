package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariffandcost.TariffAndCostClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CostUpdatedRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CostUpdatedResponse
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class TariffAndCostHandler : TariffAndCostClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun costUpdated(
        ocppSessionInfo: OcppSession.Info,
        request: CostUpdatedRequest,
    ): CostUpdatedResponse {
        stateStore.record("CostUpdated", request)
        return CostUpdatedResponse()
    }
}
