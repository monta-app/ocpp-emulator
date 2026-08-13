// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object NotifyPriorityChargingFeature : Feature {
    override val name: String = "NotifyPriorityCharging"
    override val requestType: Class<out OcppRequest> = NotifyPriorityChargingRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyPriorityChargingResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyPriorityChargingRequest(
    /** The transaction for which priority charging is requested. */
    val transactionId: String,
    /** True if priority charging was activated. False if it has stopped using the priority charging profile. */
    val activated: Boolean,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyPriorityChargingResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
