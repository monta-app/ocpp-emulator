// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class EventData(
    /** Identifies the event. This field can be referred to as a cause by other events. */
    val eventId: Int,
    /** Timestamp of the moment the report was generated. */
    val timestamp: ZonedDateTime,
    val trigger: EventTriggerEnum,
    /** Actual value (_attributeType_ Actual) of the variable. The Configuration Variable &lt;&lt;configkey-reporting-value-size,ReportingValueSize&gt;&gt; can be used to limit GetVariableResult.attributeValue, VariableAttribute.value and EventData.actualValue. The max size of these values will always remain equal. */
    val actualValue: String,
    val component: Component,
    val eventNotificationType: EventNotificationEnum,
    val variable: Variable,
    /** Refers to the Id of an event that is considered to be the cause for this event. */
    val cause: Int? = null,
    /** Technical (error) code as reported by component. */
    val techCode: String? = null,
    /** Technical detail information as reported by component. */
    val techInfo: String? = null,
    /** _Cleared_ is set to true to report the clearing of a monitored situation, i.e. a 'return to normal'. */
    val cleared: Boolean? = null,
    /** If an event notification is linked to a specific transaction, this field can be used to specify its transactionId. */
    val transactionId: String? = null,
    /** Identifies the VariableMonitoring which triggered the event. */
    val variableMonitoringId: Int? = null,
    /** *(2.1)* Severity associated with the monitor in _variableMonitoringId_ or with the hardwired notification. */
    val severity: Int? = null,
    val customData: CustomData? = null,
)
