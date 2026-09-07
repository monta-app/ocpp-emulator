// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class VariableCharacteristics(
    val dataType: DataEnum,
    /** Flag indicating if this variable supports monitoring. */
    val supportsMonitoring: Boolean,
    /** Unit of the variable. When the transmitted value has a unit, this field SHALL be included. */
    val unit: String? = null,
    /** Minimum possible value of this variable. */
    val minLimit: Double? = null,
    /** Maximum possible value of this variable. When the datatype of this Variable is String, OptionList, SequenceList or MemberList, this field defines the maximum length of the (CSV) string. */
    val maxLimit: Double? = null,
    /** *(2.1)* Maximum number of elements from _valuesList_ that are supported as _attributeValue_. */
    val maxElements: Int? = null,
    /** Mandatory when _dataType_ = OptionList, MemberList or SequenceList. In that case _valuesList_ specifies the allowed values for the type. The length of this field can be limited by DeviceDataCtrlr.ConfigurationValueSize. * OptionList: The (Actual) Variable value must be a single value from the reported (CSV) enumeration list. * MemberList: The (Actual) Variable value may be an (unordered) (sub-)set of the reported (CSV) valid values list. * SequenceList: The (Actual) Variable value may be an ordered (priority, etc) (sub-)set of the reported (CSV) valid values. This is a comma separated list. The Configuration Variable &lt;&lt;configkey-configuration-value-size,ConfigurationValueSize&gt;&gt; can be used to limit SetVariableData.attributeValue and VariableCharacteristics.valuesList. The max size of these values will always remain equal. */
    val valuesList: String? = null,
    val customData: CustomData? = null,
)
