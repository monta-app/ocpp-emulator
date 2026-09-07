// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import com.fasterxml.jackson.annotation.JsonProperty

enum class ReadingContextEnum {
    @JsonProperty("Interruption.Begin")
    InterruptionBegin,

    @JsonProperty("Interruption.End")
    InterruptionEnd,
    Other,

    @JsonProperty("Sample.Clock")
    SampleClock,

    @JsonProperty("Sample.Periodic")
    SamplePeriodic,

    @JsonProperty("Transaction.Begin")
    TransactionBegin,

    @JsonProperty("Transaction.End")
    TransactionEnd,
    Trigger,
}
