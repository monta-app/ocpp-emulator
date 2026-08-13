// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class OverstayRule(
    val overstayFee: RationalNumber,
    /** Time in seconds after trigger of the parent Overstay Rules for this particular fee to apply. */
    val startTime: Int,
    /** Time till overstay will be reapplied */
    val overstayFeePeriod: Int,
    /** Human readable string to identify the overstay rule. */
    val overstayRuleDescription: String? = null,
    val customData: CustomData? = null,
)
