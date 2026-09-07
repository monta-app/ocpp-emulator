// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class EVPowerSchedule(
    val evPowerScheduleEntries: List<EVPowerScheduleEntry>,
    /** The time that defines the starting point for the EVEnergyOffer. */
    val timeAnchor: ZonedDateTime,
    val customData: CustomData? = null,
)
