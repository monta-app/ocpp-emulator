// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class PriceRule(
    val energyFee: RationalNumber,
    val powerRangeStart: RationalNumber,
    /** The duration of the parking fee period (in seconds). When the time enters into a ParkingFeePeriod, the ParkingFee will apply to the session. . */
    val parkingFeePeriod: Int? = null,
    /** Number of grams of CO2 per kWh. */
    val carbonDioxideEmission: Int? = null,
    /** Percentage of the power that is created by renewable resources. */
    val renewableGenerationPercentage: Int? = null,
    val parkingFee: RationalNumber? = null,
    val customData: CustomData? = null,
)
