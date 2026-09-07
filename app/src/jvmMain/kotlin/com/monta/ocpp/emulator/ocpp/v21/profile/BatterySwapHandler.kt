package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.batteryswap.BatterySwapClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestBatterySwapRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.RequestBatterySwapResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class BatterySwapHandler : BatterySwapClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun requestBatterySwap(
        ocppSessionInfo: OcppSession.Info,
        request: RequestBatterySwapRequest,
    ): RequestBatterySwapResponse {
        stateStore.record("RequestBatterySwap", request)
        return RequestBatterySwapResponse(status = GenericStatusEnum.Accepted)
    }
}
