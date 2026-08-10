// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Price(
    /** Price/cost excluding tax. Can be absent if _inclTax_ is present. */
    val exclTax: Double? = null,
    /** Price/cost including tax. Can be absent if _exclTax_ is present. */
    val inclTax: Double? = null,
    val taxRates: List<TaxRate>? = null,
    val customData: CustomData? = null,
)
