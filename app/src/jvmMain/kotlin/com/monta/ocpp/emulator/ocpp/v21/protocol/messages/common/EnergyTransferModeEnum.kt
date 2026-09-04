// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import com.fasterxml.jackson.annotation.JsonProperty

enum class EnergyTransferModeEnum {
    @JsonProperty("AC_single_phase")
    ACsinglephase,

    @JsonProperty("AC_two_phase")
    ACtwophase,

    @JsonProperty("AC_three_phase")
    ACthreephase,
    DC,

    @JsonProperty("AC_BPT")
    ACBPT,

    @JsonProperty("AC_BPT_DER")
    ACBPTDER,

    @JsonProperty("AC_DER")
    ACDER,

    @JsonProperty("DC_BPT")
    DCBPT,

    @JsonProperty("DC_ACDP")
    DCACDP,

    @JsonProperty("DC_ACDP_BPT")
    DCACDPBPT,
    WPT,
}
