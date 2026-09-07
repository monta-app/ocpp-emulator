// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class EnterService(
    /** Priority of setting (0=highest) */
    val priority: Int,
    /** Enter service voltage high */
    val highVoltage: Double,
    /** Enter service voltage low */
    val lowVoltage: Double,
    /** Enter service frequency high */
    val highFreq: Double,
    /** Enter service frequency low */
    val lowFreq: Double,
    /** Enter service delay */
    val delay: Double? = null,
    /** Enter service randomized delay */
    val randomDelay: Double? = null,
    /** Enter service ramp rate in seconds */
    val rampRate: Double? = null,
    val customData: CustomData? = null,
)
