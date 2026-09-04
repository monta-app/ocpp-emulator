// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class DERCurveGet(
    val curve: DERCurve,
    /** Id of DER curve */
    val id: String,
    val curveType: DERControlEnum,
    /** True if this is a default curve */
    val isDefault: Boolean,
    /** True if this setting is superseded by a higher priority setting (i.e. lower value of _priority_) */
    val isSuperseded: Boolean,
    val customData: CustomData? = null,
)
