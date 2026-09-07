// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ReportData(
    val component: Component,
    val variable: Variable,
    val variableAttribute: List<VariableAttribute>,
    val variableCharacteristics: VariableCharacteristics? = null,
    val customData: CustomData? = null,
)
