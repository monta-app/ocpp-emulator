// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class LimitAtSoC(
    /** The SoC value beyond which the charging rate limit should be applied. */
    val soc: Int,
    /** Charging rate limit beyond the SoC value. The unit is defined by _chargingSchedule.chargingRateUnit_. */
    val limit: Double,
    val customData: CustomData? = null,
)
