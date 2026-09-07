// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class VariableAttribute(
    val type: AttributeEnum? = null,
    /** Value of the attribute. May only be omitted when mutability is set to 'WriteOnly'. The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. */
    val value: String? = null,
    val mutability: MutabilityEnum? = null,
    /** If true, value will be persistent across system reboots or power down. Default when omitted is false. */
    val persistent: Boolean? = null,
    /** If true, value that will never be changed by the Charging Station at runtime. Default when omitted is false. */
    val constant: Boolean? = null,
    val customData: CustomData? = null,
)
