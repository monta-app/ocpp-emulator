// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class PeriodicEventStreamParams(
    /** Time in seconds after which stream data is sent. */
    val interval: Int? = null,
    /** Number of items to be sent together in stream. */
    val values: Int? = null,
    val customData: CustomData? = null,
)
