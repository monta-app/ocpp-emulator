// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Hysteresis(
    /** High value for return to normal operation after a grid event, in absolute value. This value adopts the same unit as defined by yUnit */
    val hysteresisHigh: Double? = null,
    /** Low value for return to normal operation after a grid event, in absolute value. This value adopts the same unit as defined by yUnit */
    val hysteresisLow: Double? = null,
    /** Delay in seconds, once grid parameter within HysteresisLow and HysteresisHigh, for the EV to return to normal operation after a grid event. */
    val hysteresisDelay: Double? = null,
    /** Set default rate of change (ramp rate %/s) for the EV to return to normal operation after a grid event */
    val hysteresisGradient: Double? = null,
    val customData: CustomData? = null,
)
