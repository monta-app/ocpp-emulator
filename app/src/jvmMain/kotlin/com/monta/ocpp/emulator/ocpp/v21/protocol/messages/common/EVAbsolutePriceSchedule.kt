// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class EVAbsolutePriceSchedule(
    /** Starting point in time of the EVEnergyOffer. */
    val timeAnchor: ZonedDateTime,
    /** Currency code according to ISO 4217. */
    val currency: String,
    val evAbsolutePriceScheduleEntries: List<EVAbsolutePriceScheduleEntry>,
    /** ISO 15118-20 URN of price algorithm: Power, PeakPower, StackedEnergy. */
    val priceAlgorithm: String,
    val customData: CustomData? = null,
)
