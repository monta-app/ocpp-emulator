// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class CostDimension(
    val type: CostDimensionEnum,
    /** Volume of the dimension consumed, measured according to the dimension type. */
    val volume: Double,
    val customData: CustomData? = null,
)
