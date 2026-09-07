// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TariffEnergy(
    val prices: List<TariffEnergyPrice>,
    val taxRates: List<TaxRate>? = null,
    val customData: CustomData? = null,
)
