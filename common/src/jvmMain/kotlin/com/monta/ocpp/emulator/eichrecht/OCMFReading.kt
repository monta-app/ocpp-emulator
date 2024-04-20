package com.monta.ocpp.emulator.eichrecht

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class OCMFReading(

    /**
     * Time: Specification to the system time of the reading and synchronization state.
     * The time is described according to ISO 8601 with a resolution of milliseconds.
     * Accordingly, the format is according to the following scheme:
     * - `<Year>-<Month>-<Day>T<Hours>:<Minutes>:<Seconds>,<Milliseconds><Time Zone>`
     * The year is displayed with four digits. Month, day, hours, minutes and seconds is displayed with two digits.
     * Milliseconds is displayed with three digits.
     * The indication of the time zone consists of a sign and a four-digit indication for hours and minutes.
     * Example: `2018-07-24T13:22:04,000+0200`
     * The synchronization state consists of a capital letter as identifier.
     * This is added to the time, separated by a space. Available states see table 19.
     */
    @JsonProperty("TM")
    val time: String,

    /**
     * Transaction: Meter reading reason, reference of meter reading to transaction, noted as capital letter:
     * - B – Begin of transaction
     * - C – Charging = during charging (can be used optionally)
     *   - X – Exception = Error during charging, transaction continues, time and/or energy are no longer usable from this reading (incl.).
     * - E – End of transaction, alternatively more precise codes:
     *   - L – Charging process was terminated locally
     *   - R – Charging process was terminated remotely
     *   - A – (Abort) Charging process was aborted by error
     *   - P – (Power) Charging process was terminated by power failure
     * - S – Suspended = Transaction active, but currently not charging (can be used optionally)
     * - T – Tariff change <br> This field is missing if there is no transaction reference (Fiscal Metering).
     */
    @JsonProperty("TX")
    val transaction: Char? = null,

    /**
     * Reading Value: The value of the reading
     * Here the JSON data format Number is used, this allows among other things an exact marking of the valid decimal places.
     * However, the representation must not be transformed by further handling methods (e.g. processing by JSON parser)
     * (rewriting the number with a different exponent, truncation of decimal places, etc.)
     * since this would change the representation of the physical quantity and thus potentially the number of valid digits.
     * According to the application rule, it is recommended to represent the measured value with two decimal places of accuracy, if it is kWh.
     */
    @JsonProperty("RV")
    val value: Double,

    /**
     * Reading Identification: Identifier, which quantity was read, according to OBIS code.
     */
    @JsonProperty("RI")
    val identification: String? = null,

    /**
     * Reading Unit: Unit of reading, e.g. kWh, according to table 20: Predefined Units.
     */
    @JsonProperty("RU")
    val unit: String,

    /**
     * Reading Current Type: The type of current measured by the meter, e.g. alternating current or direct current, according to table 21:
     * Predefined Current Types.
     * This field is optional. No default value is defined.
     */
    @JsonProperty("RT")
    val currenType: String? = null,

    /**
     * Cumulated Loss: This parameter is optional and can be added only when RI is indicating an accumulation register reading.
     * The value reported here represents cumulated loss withdrawned from measurement when computing loss compensation on RV.
     * CL must be reset at TX=B. CL is given in the same unit as RV which is specified in RU.
     */
    @JsonProperty("CL")
    val cumulatedLoss: Number? = null,

    /**
     * Error Flags: Statement about which quantities are no longer usable for billing due to an error.
     * Each character in this string identifies a quantity. The following characters are defined:
     * - E – Energy
     * - t – Time
     */
    @JsonProperty("EF")
    val errorFlags: String? = null,

    /**
     * Status: State of the meter at the time of reading. Noted as abbreviation according to table 10.
     */
    @JsonProperty("ST")
    val status: Char
) {
    companion object {
        private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSSZ")

        fun formatReadingTime(time: Instant, synchronizationState: Char): String {
            return "${DATE_FORMAT.format(ZonedDateTime.ofInstant(time.truncatedTo(ChronoUnit.MILLIS), ZoneId.systemDefault()))} $synchronizationState"
        }
    }
}
