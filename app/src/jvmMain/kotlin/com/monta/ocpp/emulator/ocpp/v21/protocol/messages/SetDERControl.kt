// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DERControlEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DERControlStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DERCurve
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.EnterService
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.FixedPF
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.FixedVar
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.FreqDroop
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Gradient
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.LimitMaxDischarge
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object SetDERControlFeature : Feature {
    override val name: String = "SetDERControl"
    override val requestType: Class<out OcppRequest> = SetDERControlRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = SetDERControlResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetDERControlRequest(
    /** True if this is a default DER control */
    val isDefault: Boolean,
    /** Unique id of this control, e.g. UUID */
    val controlId: String,
    val controlType: DERControlEnum,
    val curve: DERCurve? = null,
    val enterService: EnterService? = null,
    val fixedPFAbsorb: FixedPF? = null,
    val fixedPFInject: FixedPF? = null,
    val fixedVar: FixedVar? = null,
    val freqDroop: FreqDroop? = null,
    val gradient: Gradient? = null,
    val limitMaxDischarge: LimitMaxDischarge? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetDERControlResponse(
    val status: DERControlStatusEnum,
    val statusInfo: StatusInfo? = null,
    /** List of controlIds that are superseded as a result of setting this control. */
    val supersededIds: List<String>? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
