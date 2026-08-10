// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TariffFixedPrice(
    /** Fixed price for this element e.g. a start fee. */
    val priceFixed: Double,
    val conditions: TariffConditionsFixed? = null,
    val customData: CustomData? = null,
)
