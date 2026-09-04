// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class AdditionalSelectedServices(
    val serviceFee: RationalNumber,
    /** Human readable string to identify this service. */
    val serviceName: String,
    val customData: CustomData? = null,
)
