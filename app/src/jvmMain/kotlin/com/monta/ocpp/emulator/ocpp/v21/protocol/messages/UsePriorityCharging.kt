// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.PriorityChargingStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object UsePriorityChargingFeature : Feature {
    override val name: String = "UsePriorityCharging"
    override val requestType: Class<out OcppRequest> = UsePriorityChargingRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = UsePriorityChargingResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class UsePriorityChargingRequest(
    /** The transaction for which priority charging is requested. */
    val transactionId: String,
    /** True to request priority charging. False to request stopping priority charging. */
    val activate: Boolean,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class UsePriorityChargingResponse(
    val status: PriorityChargingStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
