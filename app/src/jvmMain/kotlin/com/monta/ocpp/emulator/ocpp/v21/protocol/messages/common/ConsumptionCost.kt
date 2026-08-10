// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ConsumptionCost(
    /** The lowest level of consumption that defines the starting point of this consumption block. The block interval extends to the start of the next interval. */
    val startValue: Double,
    val cost: List<Cost>,
    val customData: CustomData? = null,
)
