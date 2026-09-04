// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import com.fasterxml.jackson.annotation.JsonProperty

enum class PhaseEnum {
    L1,
    L2,
    L3,
    N,

    @JsonProperty("L1-N")
    L1N,

    @JsonProperty("L2-N")
    L2N,

    @JsonProperty("L3-N")
    L3N,

    @JsonProperty("L1-L2")
    L1L2,

    @JsonProperty("L2-L3")
    L2L3,

    @JsonProperty("L3-L1")
    L3L1,
}
