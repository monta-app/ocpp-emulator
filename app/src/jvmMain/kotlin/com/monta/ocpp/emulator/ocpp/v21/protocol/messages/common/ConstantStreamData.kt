// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ConstantStreamData(
    /** Uniquely identifies the stream */
    val id: Int,
    val params: PeriodicEventStreamParams,
    /** Id of monitor used to report his event. It can be a preconfigured or hardwired monitor. */
    val variableMonitoringId: Int,
    val customData: CustomData? = null,
)
