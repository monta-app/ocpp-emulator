// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object ClearedChargingLimitFeature : Feature {
    override val name: String = "ClearedChargingLimit"
    override val requestType: Class<out OcppRequest> = ClearedChargingLimitRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ClearedChargingLimitResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearedChargingLimitRequest(
    /** Source of the charging limit. Allowed values defined in Appendix as ChargingLimitSourceEnumStringType. */
    val chargingLimitSource: String,
    /** EVSE Identifier. */
    val evseId: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearedChargingLimitResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
