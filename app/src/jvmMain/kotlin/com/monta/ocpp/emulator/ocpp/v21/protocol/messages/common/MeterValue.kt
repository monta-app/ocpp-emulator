// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class MeterValue(
    val sampledValue: List<SampledValue>,
    /** Timestamp for measured value(s). */
    val timestamp: ZonedDateTime,
    val customData: CustomData? = null,
)
