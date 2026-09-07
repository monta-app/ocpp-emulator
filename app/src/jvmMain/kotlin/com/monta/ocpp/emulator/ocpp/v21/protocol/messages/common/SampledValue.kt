// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class SampledValue(
    /** Indicates the measured value. */
    val value: Double,
    val measurand: MeasurandEnum? = null,
    val context: ReadingContextEnum? = null,
    val phase: PhaseEnum? = null,
    val location: LocationEnum? = null,
    val signedMeterValue: SignedMeterValue? = null,
    val unitOfMeasure: UnitOfMeasure? = null,
    val customData: CustomData? = null,
)
