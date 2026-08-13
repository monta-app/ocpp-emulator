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

object GetDERControlFeature : Feature {
    override val name: String = "GetDERControl"
    override val requestType: Class<out OcppRequest> = GetDERControlRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetDERControlResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetDERControlRequest(
    /** RequestId to be used in ReportDERControlRequest. */
    val requestId: Int,
    /** True: get a default DER control. False: get a scheduled control. */
    val isDefault: Boolean? = null,
    val controlType: DERControlEnum? = null,
    /** Id of setting to get. When omitted all settings for _controlType_ are retrieved. */
    val controlId: String? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetDERControlResponse(
    val status: DERControlStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
