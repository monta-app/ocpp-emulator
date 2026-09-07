// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class V2XFreqWattPoint(
    /** Net frequency in Hz. */
    val frequency: Double,
    /** Power in W to charge (positive) or discharge (negative) at specified frequency. */
    val power: Double,
    val customData: CustomData? = null,
)
