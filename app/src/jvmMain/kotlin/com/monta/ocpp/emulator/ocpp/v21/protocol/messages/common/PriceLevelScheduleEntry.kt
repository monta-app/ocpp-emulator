// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class PriceLevelScheduleEntry(
    /** The amount of seconds that define the duration of this given PriceLevelScheduleEntry. */
    val duration: Int,
    /** Defines the price level of this PriceLevelScheduleEntry (referring to NumberOfPriceLevels). Small values for the PriceLevel represent a cheaper PriceLevelScheduleEntry. Large values for the PriceLevel represent a more expensive PriceLevelScheduleEntry. */
    val priceLevel: Int,
    val customData: CustomData? = null,
)
