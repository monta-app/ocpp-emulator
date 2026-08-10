// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class OverstayRuleList(
    val overstayRule: List<OverstayRule>,
    val overstayPowerThreshold: RationalNumber? = null,
    /** Time till overstay is applied in seconds. */
    val overstayTimeThreshold: Int? = null,
    val customData: CustomData? = null,
)
