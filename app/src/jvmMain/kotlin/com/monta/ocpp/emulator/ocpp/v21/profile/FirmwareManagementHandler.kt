package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.firmwaremanagement.FirmwareManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnpublishFirmwareRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnpublishFirmwareResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateFirmwareRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateFirmwareResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.UnpublishFirmwareStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.UpdateFirmwareStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class FirmwareManagementHandler : FirmwareManagementClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun publishFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: PublishFirmwareRequest,
    ): PublishFirmwareResponse {
        stateStore.record("PublishFirmware", request)
        return PublishFirmwareResponse(status = GenericStatusEnum.Accepted)
    }

    override suspend fun unpublishFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: UnpublishFirmwareRequest,
    ): UnpublishFirmwareResponse {
        stateStore.record("UnpublishFirmware", request)
        return UnpublishFirmwareResponse(status = UnpublishFirmwareStatusEnum.Unpublished)
    }

    override suspend fun updateFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: UpdateFirmwareRequest,
    ): UpdateFirmwareResponse {
        stateStore.record("UpdateFirmware", request)
        return UpdateFirmwareResponse(status = UpdateFirmwareStatusEnum.Accepted)
    }
}
