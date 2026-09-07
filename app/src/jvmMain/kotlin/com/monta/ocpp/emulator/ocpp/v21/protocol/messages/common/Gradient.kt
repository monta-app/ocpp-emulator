// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Gradient(
    /** Id of setting */
    val priority: Int,
    /** Default ramp rate in seconds (0 if not applicable) */
    val gradient: Double,
    /** Soft-start ramp rate in seconds (0 if not applicable) */
    val softGradient: Double,
    val customData: CustomData? = null,
)
