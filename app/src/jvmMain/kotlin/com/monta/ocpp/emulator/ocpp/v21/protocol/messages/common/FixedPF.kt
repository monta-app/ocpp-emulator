// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class FixedPF(
    /** Priority of setting (0=highest) */
    val priority: Int,
    /** Power factor, cos(phi), as value between 0..1. */
    val displacement: Double,
    /** True when absorbing reactive power (under-excited), false when injecting reactive power (over-excited). */
    val excitation: Boolean,
    /** Time when this setting becomes active */
    val startTime: ZonedDateTime? = null,
    /** Duration in seconds that this setting is active. */
    val duration: Double? = null,
    val customData: CustomData? = null,
)
