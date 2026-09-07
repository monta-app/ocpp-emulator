// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TariffConditions(
    /** Start time of day in local time. + Format as per RFC 3339: time-hour ":" time-minute + Must be in 24h format with leading zeros. Hour/Minute separator: ":" Regex: ([0-1][0-9]\|2[0-3]):[0-5][0-9] */
    val startTimeOfDay: String? = null,
    /** End time of day in local time. Same syntax as _startTimeOfDay_. + If end time &lt; start time then the period wraps around to the next day. + To stop at end of the day use: 00:00. */
    val endTimeOfDay: String? = null,
    /** Day(s) of the week this is tariff applies. */
    val dayOfWeek: List<DayOfWeekEnum>? = null,
    /** Start date in local time, for example: 2015-12-24. Valid from this day (inclusive). + Format as per RFC 3339: full-date + Regex: ([12][0-9]{3})-(0[1-9]\|1[0-2])-(0[1-9]\|[12][0-9]\|3[01]) */
    val validFromDate: String? = null,
    /** End date in local time, for example: 2015-12-27. Valid until this day (exclusive). Same syntax as _validFromDate_. */
    val validToDate: String? = null,
    val evseKind: EvseKindEnum? = null,
    /** Minimum consumed energy in Wh, for example 20000 Wh. Valid from this amount of energy (inclusive) being used. */
    val minEnergy: Double? = null,
    /** Maximum consumed energy in Wh, for example 50000 Wh. Valid until this amount of energy (exclusive) being used. */
    val maxEnergy: Double? = null,
    /** Sum of the minimum current (in Amperes) over all phases, for example 5 A. When the EV is charging with more than, or equal to, the defined amount of current, this price is/becomes active. If the charging current is or becomes lower, this price is not or no longer valid and becomes inactive. + This is NOT about the minimum current over the entire transaction. */
    val minCurrent: Double? = null,
    /** Sum of the maximum current (in Amperes) over all phases, for example 20 A. When the EV is charging with less than the defined amount of current, this price becomes/is active. If the charging current is or becomes higher, this price is not or no longer valid and becomes inactive. This is NOT about the maximum current over the entire transaction. */
    val maxCurrent: Double? = null,
    /** Minimum power in W, for example 5000 W. When the EV is charging with more than, or equal to, the defined amount of power, this price is/becomes active. If the charging power is or becomes lower, this price is not or no longer valid and becomes inactive. This is NOT about the minimum power over the entire transaction. */
    val minPower: Double? = null,
    /** Maximum power in W, for example 20000 W. When the EV is charging with less than the defined amount of power, this price becomes/is active. If the charging power is or becomes higher, this price is not or no longer valid and becomes inactive. This is NOT about the maximum power over the entire transaction. */
    val maxPower: Double? = null,
    /** Minimum duration in seconds the transaction (charging &amp; idle) MUST last (inclusive). When the duration of a transaction is longer than the defined value, this price is or becomes active. Before that moment, this price is not yet active. */
    val minTime: Int? = null,
    /** Maximum duration in seconds the transaction (charging &amp; idle) MUST last (exclusive). When the duration of a transaction is shorter than the defined value, this price is or becomes active. After that moment, this price is no longer active. */
    val maxTime: Int? = null,
    /** Minimum duration in seconds the charging MUST last (inclusive). When the duration of a charging is longer than the defined value, this price is or becomes active. Before that moment, this price is not yet active. */
    val minChargingTime: Int? = null,
    /** Maximum duration in seconds the charging MUST last (exclusive). When the duration of a charging is shorter than the defined value, this price is or becomes active. After that moment, this price is no longer active. */
    val maxChargingTime: Int? = null,
    /** Minimum duration in seconds the idle period (i.e. not charging) MUST last (inclusive). When the duration of the idle time is longer than the defined value, this price is or becomes active. Before that moment, this price is not yet active. */
    val minIdleTime: Int? = null,
    /** Maximum duration in seconds the idle period (i.e. not charging) MUST last (exclusive). When the duration of idle time is shorter than the defined value, this price is or becomes active. After that moment, this price is no longer active. */
    val maxIdleTime: Int? = null,
    val customData: CustomData? = null,
)
