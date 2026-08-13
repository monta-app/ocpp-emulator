// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import java.time.ZonedDateTime

object SecurityEventNotificationFeature : Feature {
    override val name: String = "SecurityEventNotification"
    override val requestType: Class<out OcppRequest> = SecurityEventNotificationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = SecurityEventNotificationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SecurityEventNotificationRequest(
    /** Type of the security event. This value should be taken from the Security events list. */
    val type: String,
    /** Date and time at which the event occurred. */
    val timestamp: ZonedDateTime,
    /** Additional information about the occurred security event. */
    val techInfo: String? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SecurityEventNotificationResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
