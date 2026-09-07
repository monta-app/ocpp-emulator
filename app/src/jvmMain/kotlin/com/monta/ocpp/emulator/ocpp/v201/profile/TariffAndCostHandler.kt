package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.tariffandcost.CostUpdatedRequest
import com.monta.library.ocpp.v201.blocks.tariffandcost.CostUpdatedResponse
import com.monta.library.ocpp.v201.blocks.tariffandcost.TariffAndCostClientDispatcher
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import javax.inject.Singleton

@Singleton
class TariffAndCostHandler(
    private val chargePointService: ChargePointService,
) : TariffAndCostClientDispatcher.Listener {
    override suspend fun costUpdated(
        ocppSessionInfo: OcppSession.Info,
        request: CostUpdatedRequest,
    ): CostUpdatedResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return CostUpdatedResponse()
    }
}
