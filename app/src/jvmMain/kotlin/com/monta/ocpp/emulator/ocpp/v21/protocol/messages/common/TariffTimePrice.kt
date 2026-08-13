// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TariffTimePrice(
    /** Price per minute (excl. tax) for this element. */
    val priceMinute: Double,
    val conditions: TariffConditions? = null,
    val customData: CustomData? = null,
)
