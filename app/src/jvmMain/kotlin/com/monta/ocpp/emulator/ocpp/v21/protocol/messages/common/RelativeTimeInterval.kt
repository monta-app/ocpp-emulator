// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class RelativeTimeInterval(
    /** Start of the interval, in seconds from NOW. */
    val start: Int,
    /** Duration of the interval, in seconds. */
    val duration: Int? = null,
    val customData: CustomData? = null,
)
