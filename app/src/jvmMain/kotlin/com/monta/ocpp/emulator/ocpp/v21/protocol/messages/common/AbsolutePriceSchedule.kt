// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class AbsolutePriceSchedule(
    /** Starting point of price schedule. */
    val timeAnchor: ZonedDateTime,
    /** Unique ID of price schedule */
    val priceScheduleID: Int,
    /** Currency according to ISO 4217. */
    val currency: String,
    /** String that indicates what language is used for the human readable strings in the price schedule. Based on ISO 639. */
    val language: String,
    /** A string in URN notation which shall uniquely identify an algorithm that defines how to compute an energy fee sum for a specific power profile based on the EnergyFee information from the PriceRule elements. */
    val priceAlgorithm: String,
    val priceRuleStacks: List<PriceRuleStack>,
    /** Description of the price schedule. */
    val priceScheduleDescription: String? = null,
    val minimumCost: RationalNumber? = null,
    val maximumCost: RationalNumber? = null,
    val taxRules: List<TaxRule>? = null,
    val overstayRuleList: OverstayRuleList? = null,
    val additionalSelectedServices: List<AdditionalSelectedServices>? = null,
    val customData: CustomData? = null,
)
