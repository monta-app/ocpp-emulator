// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Cost(
    val costKind: CostKindEnum,
    /** The estimated or actual cost per kWh */
    val amount: Int,
    /** Values: -3..3, The amountMultiplier defines the exponent to base 10 (dec). The final value is determined by: amount * 10 ^ amountMultiplier */
    val amountMultiplier: Int? = null,
    val customData: CustomData? = null,
)
