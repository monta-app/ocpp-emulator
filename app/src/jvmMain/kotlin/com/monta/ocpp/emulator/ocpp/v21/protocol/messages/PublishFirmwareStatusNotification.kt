// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.PublishFirmwareStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object PublishFirmwareStatusNotificationFeature : Feature {
    override val name: String = "PublishFirmwareStatusNotification"
    override val requestType: Class<out OcppRequest> = PublishFirmwareStatusNotificationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = PublishFirmwareStatusNotificationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class PublishFirmwareStatusNotificationRequest(
    val status: PublishFirmwareStatusEnum,
    /** Required if status is Published. Can be multiple URI’s, if the Local Controller supports e.g. HTTP, HTTPS, and FTP. */
    val location: List<String>? = null,
    /** The request id that was provided in the PublishFirmwareRequest which triggered this action. */
    val requestId: Int? = null,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class PublishFirmwareStatusNotificationResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
