// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class GetVariableResult(
    val attributeStatus: GetVariableStatusEnum,
    val component: Component,
    val variable: Variable,
    val attributeStatusInfo: StatusInfo? = null,
    val attributeType: AttributeEnum? = null,
    /** Value of requested attribute type of component-variable. This field can only be empty when the given status is NOT accepted. The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. */
    val attributeValue: String? = null,
    val customData: CustomData? = null,
)
