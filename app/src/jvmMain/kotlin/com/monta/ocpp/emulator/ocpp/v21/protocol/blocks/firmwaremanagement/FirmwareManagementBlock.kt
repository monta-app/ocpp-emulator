// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.firmwaremanagement

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.FirmwareStatusNotificationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.FirmwareStatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.FirmwareStatusNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareStatusNotificationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareStatusNotificationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.PublishFirmwareStatusNotificationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnpublishFirmwareFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnpublishFirmwareRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UnpublishFirmwareResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateFirmwareFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateFirmwareRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.UpdateFirmwareResponse

val firmwaremanagementFeatures = listOf(
    FirmwareStatusNotificationFeature,
    PublishFirmwareStatusNotificationFeature,
    PublishFirmwareFeature,
    UnpublishFirmwareFeature,
    UpdateFirmwareFeature,
)

class FirmwareManagementClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = firmwaremanagementFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is PublishFirmwareRequest -> listener.publishFirmware(ocppSessionInfo, request)
            is UnpublishFirmwareRequest -> listener.unpublishFirmware(ocppSessionInfo, request)
            is UpdateFirmwareRequest -> listener.updateFirmware(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun publishFirmware(
            ocppSessionInfo: OcppSession.Info,
            request: PublishFirmwareRequest,
        ): PublishFirmwareResponse

        suspend fun unpublishFirmware(
            ocppSessionInfo: OcppSession.Info,
            request: UnpublishFirmwareRequest,
        ): UnpublishFirmwareResponse

        suspend fun updateFirmware(
            ocppSessionInfo: OcppSession.Info,
            request: UpdateFirmwareRequest,
        ): UpdateFirmwareResponse
    }

    interface Sender {
        suspend fun firmwareStatusNotification(
            request: FirmwareStatusNotificationRequest,
        ): FirmwareStatusNotificationResponse

        suspend fun publishFirmwareStatusNotification(
            request: PublishFirmwareStatusNotificationRequest,
        ): PublishFirmwareStatusNotificationResponse
    }
}
