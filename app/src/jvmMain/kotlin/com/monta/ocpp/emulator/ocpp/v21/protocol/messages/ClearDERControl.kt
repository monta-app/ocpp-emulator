// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DERControlEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DERControlStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object ClearDERControlFeature : Feature {
    override val name: String = "ClearDERControl"
    override val requestType: Class<out OcppRequest> = ClearDERControlRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ClearDERControlResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearDERControlRequest(
    /** True: clearing default DER controls. False: clearing scheduled controls. */
    val isDefault: Boolean,
    val controlType: DERControlEnum? = null,
    /** Id of control setting to clear. When omitted all settings for _controlType_ are cleared. */
    val controlId: String? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearDERControlResponse(
    val status: DERControlStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
