// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class RationalNumber(
    /** The exponent to base 10 (dec) */
    val exponent: Int,
    /** Value which shall be multiplied. */
    val value: Int,
    val customData: CustomData? = null,
)
