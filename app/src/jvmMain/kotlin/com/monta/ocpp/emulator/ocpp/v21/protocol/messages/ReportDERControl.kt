// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DERCurveGet
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.EnterServiceGet
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.FixedPFGet
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.FixedVarGet
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.FreqDroopGet
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GradientGet
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.LimitMaxDischargeGet

object ReportDERControlFeature : Feature {
    override val name: String = "ReportDERControl"
    override val requestType: Class<out OcppRequest> = ReportDERControlRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ReportDERControlResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ReportDERControlRequest(
    /** RequestId from GetDERControlRequest. */
    val requestId: Int,
    val curve: List<DERCurveGet>? = null,
    val enterService: List<EnterServiceGet>? = null,
    val fixedPFAbsorb: List<FixedPFGet>? = null,
    val fixedPFInject: List<FixedPFGet>? = null,
    val fixedVar: List<FixedVarGet>? = null,
    val freqDroop: List<FreqDroopGet>? = null,
    val gradient: List<GradientGet>? = null,
    val limitMaxDischarge: List<LimitMaxDischargeGet>? = null,
    /** To Be Continued. Default value when omitted: false. + False indicates that there are no further messages as part of this report. */
    val tbc: Boolean? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ReportDERControlResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
