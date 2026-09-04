// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class MessageInfo(
    /** Unique id within an exchange context. It is defined within the OCPP context as a positive Integer value (greater or equal to zero). */
    val id: Int,
    val priority: MessagePriorityEnum,
    val message: MessageContent,
    val display: Component? = null,
    val state: MessageStateEnum? = null,
    /** From what date-time should this message be shown. If omitted: directly. */
    val startDateTime: ZonedDateTime? = null,
    /** Until what date-time should this message be shown, after this date/time this message SHALL be removed. */
    val endDateTime: ZonedDateTime? = null,
    /** During which transaction shall this message be shown. Message SHALL be removed by the Charging Station after transaction has ended. */
    val transactionId: String? = null,
    val messageExtra: List<MessageContent>? = null,
    val customData: CustomData? = null,
)
