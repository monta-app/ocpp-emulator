// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.FirmwareStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object FirmwareStatusNotificationFeature : Feature {
    override val name: String = "FirmwareStatusNotification"
    override val requestType: Class<out OcppRequest> = FirmwareStatusNotificationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = FirmwareStatusNotificationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class FirmwareStatusNotificationRequest(
    val status: FirmwareStatusEnum,
    /** The request id that was provided in the UpdateFirmwareRequest that started this firmware update. This field is mandatory, unless the message was triggered by a TriggerMessageRequest AND there is no firmware update ongoing. */
    val requestId: Int? = null,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class FirmwareStatusNotificationResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
