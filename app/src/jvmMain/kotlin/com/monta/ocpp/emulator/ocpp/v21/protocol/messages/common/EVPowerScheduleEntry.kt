// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class EVPowerScheduleEntry(
    /** The duration of this entry. */
    val duration: Int,
    /** Defines maximum amount of power for the duration of this EVPowerScheduleEntry to be discharged from the EV battery through EVSE power outlet. Negative values are used for discharging. */
    val power: Double,
    val customData: CustomData? = null,
)
