// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ChargingLimit(
    /** Represents the source of the charging limit. Values defined in appendix as ChargingLimitSourceEnumStringType. */
    val chargingLimitSource: String,
    /** *(2.1)* True when the reported limit concerns local generation that is providing extra capacity, instead of a limitation. */
    val isLocalGeneration: Boolean? = null,
    /** Indicates whether the charging limit is critical for the grid. */
    val isGridCritical: Boolean? = null,
    val customData: CustomData? = null,
)
