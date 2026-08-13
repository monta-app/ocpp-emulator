// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class SalesTariff(
    /** SalesTariff identifier used to identify one sales tariff. An SAID remains a unique identifier for one schedule throughout a charging session. */
    val id: Int,
    val salesTariffEntry: List<SalesTariffEntry>,
    /** A human readable title/short description of the sales tariff e.g. for HMI display purposes. */
    val salesTariffDescription: String? = null,
    /** Defines the overall number of distinct price levels used across all provided SalesTariff elements. */
    val numEPriceLevels: Int? = null,
    val customData: CustomData? = null,
)
