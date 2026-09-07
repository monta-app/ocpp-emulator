// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class FreqDroop(
    /** Priority of setting (0=highest) */
    val priority: Int,
    /** Over-frequency start of droop */
    val overFreq: Double,
    /** Under-frequency start of droop */
    val underFreq: Double,
    /** Over-frequency droop per unit, oFDroop */
    val overDroop: Double,
    /** Under-frequency droop per unit, uFDroop */
    val underDroop: Double,
    /** Open loop response time in seconds */
    val responseTime: Double,
    /** Time when this setting becomes active */
    val startTime: ZonedDateTime? = null,
    /** Duration in seconds that this setting is active */
    val duration: Double? = null,
    val customData: CustomData? = null,
)
