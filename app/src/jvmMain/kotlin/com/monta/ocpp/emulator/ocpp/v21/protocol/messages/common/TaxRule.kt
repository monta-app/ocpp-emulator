// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TaxRule(
    /** Id for the tax rule. */
    val taxRuleID: Int,
    /** Indicates whether this tax applies to Energy Fees. */
    val appliesToEnergyFee: Boolean,
    /** Indicates whether this tax applies to Parking Fees. */
    val appliesToParkingFee: Boolean,
    /** Indicates whether this tax applies to Overstay Fees. */
    val appliesToOverstayFee: Boolean,
    /** Indicates whether this tax applies to Minimum/Maximum Cost. */
    val appliesToMinimumMaximumCost: Boolean,
    val taxRate: RationalNumber,
    /** Human readable string to identify the tax rule. */
    val taxRuleName: String? = null,
    /** Indicates whether the tax is included in any price or not. */
    val taxIncludedInPrice: Boolean? = null,
    val customData: CustomData? = null,
)
