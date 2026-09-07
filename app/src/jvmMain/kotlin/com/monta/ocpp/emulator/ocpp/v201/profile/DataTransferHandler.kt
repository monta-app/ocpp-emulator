package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.datatransfer.DataTransferClientDispatcher
import com.monta.library.ocpp.v201.blocks.datatransfer.DataTransferRequest
import com.monta.library.ocpp.v201.blocks.datatransfer.DataTransferResponse
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import javax.inject.Singleton

@Singleton
class DataTransferHandler(
    private val chargePointService: ChargePointService,
) : DataTransferClientDispatcher.Listener {
    override suspend fun dataTransfer(
        ocppSessionInfo: OcppSession.Info,
        request: DataTransferRequest,
    ): DataTransferResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return DataTransferResponse(status = DataTransferResponse.Status.Accepted)
    }
}
