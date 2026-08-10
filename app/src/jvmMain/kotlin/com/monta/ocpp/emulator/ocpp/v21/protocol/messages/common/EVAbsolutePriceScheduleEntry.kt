// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class EVAbsolutePriceScheduleEntry(
    /** The amount of seconds of this entry. */
    val duration: Int,
    val evPriceRule: List<EVPriceRule>,
    val customData: CustomData? = null,
)
