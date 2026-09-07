// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class SalesTariffEntry(
    val relativeTimeInterval: RelativeTimeInterval,
    /** Defines the price level of this SalesTariffEntry (referring to NumEPriceLevels). Small values for the EPriceLevel represent a cheaper TariffEntry. Large values for the EPriceLevel represent a more expensive TariffEntry. */
    val ePriceLevel: Int? = null,
    val consumptionCost: List<ConsumptionCost>? = null,
    val customData: CustomData? = null,
)
