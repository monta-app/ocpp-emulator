// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class SetVariableData(
    /** Value to be assigned to attribute of variable. This value is allowed to be an empty string (""). The Configuration Variable &lt;&lt;configkey-configuration-value-size,ConfigurationValueSize&gt;&gt; can be used to limit SetVariableData.attributeValue and VariableCharacteristics.valuesList. The max size of these values will always remain equal. */
    val attributeValue: String,
    val component: Component,
    val variable: Variable,
    val attributeType: AttributeEnum? = null,
    val customData: CustomData? = null,
)
