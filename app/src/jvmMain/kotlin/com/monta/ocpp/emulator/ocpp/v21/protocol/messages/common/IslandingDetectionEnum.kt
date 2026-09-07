// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import com.fasterxml.jackson.annotation.JsonProperty

enum class IslandingDetectionEnum {
    NoAntiIslandingSupport,
    RoCoF,

    @JsonProperty("UVP_OVP")
    UVPOVP,

    @JsonProperty("UFP_OFP")
    UFPOFP,
    VoltageVectorShift,
    ZeroCrossingDetection,
    OtherPassive,
    ImpedanceMeasurement,
    ImpedanceAtFrequency,
    SlipModeFrequencyShift,
    SandiaFrequencyShift,
    SandiaVoltageShift,
    FrequencyJump,
    RCLQFactor,
    OtherActive,
}
