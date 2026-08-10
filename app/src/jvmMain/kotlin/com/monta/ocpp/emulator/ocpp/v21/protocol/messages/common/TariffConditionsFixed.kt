// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TariffConditionsFixed(
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
    /** For which payment brand this (adhoc) tariff applies. Can be used to add a surcharge for certain payment brands. Based on value of _additionalIdToken_ from _idToken.additionalInfo.type_ = "PaymentBrand". */
    val paymentBrand: String? = null,
    /** Type of adhoc payment, e.g. CC, Debit. Based on value of _additionalIdToken_ from _idToken.additionalInfo.type_ = "PaymentRecognition". */
    val paymentRecognition: String? = null,
    val customData: CustomData? = null,
)
