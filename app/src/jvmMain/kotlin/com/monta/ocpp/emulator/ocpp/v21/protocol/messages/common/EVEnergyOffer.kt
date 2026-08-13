// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class EVEnergyOffer(
    val evPowerSchedule: EVPowerSchedule,
    val evAbsolutePriceSchedule: EVAbsolutePriceSchedule? = null,
    val customData: CustomData? = null,
)
