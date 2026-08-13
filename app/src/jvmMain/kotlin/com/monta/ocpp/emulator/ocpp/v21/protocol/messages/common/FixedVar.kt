// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class FixedVar(
    /** Priority of setting (0=highest) */
    val priority: Int,
    /** The value specifies a target var output interpreted as a signed percentage (-100 to 100). A negative value refers to charging, whereas a positive one refers to discharging. The value type is determined by the unit field. */
    val setpoint: Double,
    val unit: DERUnitEnum,
    /** Time when this setting becomes active. */
    val startTime: ZonedDateTime? = null,
    /** Duration in seconds that this setting is active. */
    val duration: Double? = null,
    val customData: CustomData? = null,
)
