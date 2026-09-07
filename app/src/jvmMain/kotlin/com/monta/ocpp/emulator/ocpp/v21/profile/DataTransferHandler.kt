package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.datatransfer.DataTransferClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DataTransferRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DataTransferResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DataTransferStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class DataTransferHandler : DataTransferClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun dataTransfer(
        ocppSessionInfo: OcppSession.Info,
        request: DataTransferRequest,
    ): DataTransferResponse {
        stateStore.record("DataTransfer", request)
        return DataTransferResponse(status = DataTransferStatusEnum.Accepted)
    }
}
