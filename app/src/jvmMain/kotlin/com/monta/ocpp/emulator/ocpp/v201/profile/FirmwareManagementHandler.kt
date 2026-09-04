package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.firmwaremanagement.FirmwareManagementClientDispatcher
import com.monta.library.ocpp.v201.blocks.firmwaremanagement.PublishFirmwareRequest
import com.monta.library.ocpp.v201.blocks.firmwaremanagement.PublishFirmwareResponse
import com.monta.library.ocpp.v201.blocks.firmwaremanagement.UnpublishFirmwareRequest
import com.monta.library.ocpp.v201.blocks.firmwaremanagement.UnpublishFirmwareResponse
import com.monta.library.ocpp.v201.blocks.firmwaremanagement.UpdateFirmwareRequest
import com.monta.library.ocpp.v201.blocks.firmwaremanagement.UpdateFirmwareResponse
import com.monta.library.ocpp.v201.common.GenericStatus
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import javax.inject.Singleton

@Singleton
class FirmwareManagementHandler(
    private val chargePointService: ChargePointService,
) : FirmwareManagementClientDispatcher.Listener {
    override suspend fun publishFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: PublishFirmwareRequest,
    ): PublishFirmwareResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return PublishFirmwareResponse(status = GenericStatus.Accepted)
    }

    override suspend fun unpublishFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: UnpublishFirmwareRequest,
    ): UnpublishFirmwareResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return UnpublishFirmwareResponse(status = UnpublishFirmwareResponse.Status.Unpublished)
    }

    override suspend fun updateFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: UpdateFirmwareRequest,
    ): UpdateFirmwareResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return UpdateFirmwareResponse(status = UpdateFirmwareResponse.Status.Accepted)
    }
}
