// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class PriceLevelSchedule(
    val priceLevelScheduleEntries: List<PriceLevelScheduleEntry>,
    /** Starting point of this price schedule. */
    val timeAnchor: ZonedDateTime,
    /** Unique ID of this price schedule. */
    val priceScheduleId: Int,
    /** Defines the overall number of distinct price level elements used across all PriceLevelSchedules. */
    val numberOfPriceLevels: Int,
    /** Description of the price schedule. */
    val priceScheduleDescription: String? = null,
    val customData: CustomData? = null,
)
