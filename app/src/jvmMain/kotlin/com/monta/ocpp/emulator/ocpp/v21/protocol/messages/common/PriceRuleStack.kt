// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class PriceRuleStack(
    /** Duration of the stack of price rules. he amount of seconds that define the duration of the given PriceRule(s). */
    val duration: Int,
    val priceRule: List<PriceRule>,
    val customData: CustomData? = null,
)
