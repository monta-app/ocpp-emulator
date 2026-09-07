// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.UploadLogStatusEnum

object LogStatusNotificationFeature : Feature {
    override val name: String = "LogStatusNotification"
    override val requestType: Class<out OcppRequest> = LogStatusNotificationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = LogStatusNotificationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class LogStatusNotificationRequest(
    val status: UploadLogStatusEnum,
    /** The request id that was provided in GetLogRequest that started this log upload. This field is mandatory, unless the message was triggered by a TriggerMessageRequest AND there is no log upload ongoing. */
    val requestId: Int? = null,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class LogStatusNotificationResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
