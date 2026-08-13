package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.afrr.AfrrClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AFRRSignalRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.AFRRSignalResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class AfrrHandler : AfrrClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun aFRRSignal(
        ocppSessionInfo: OcppSession.Info,
        request: AFRRSignalRequest,
    ): AFRRSignalResponse {
        stateStore.record("AFRRSignal", request)
        return AFRRSignalResponse(status = GenericStatusEnum.Accepted)
    }
}
