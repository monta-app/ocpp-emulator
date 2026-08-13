// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class MonitoringData(
    val component: Component,
    val variable: Variable,
    val variableMonitoring: List<VariableMonitoring>,
    val customData: CustomData? = null,
)
