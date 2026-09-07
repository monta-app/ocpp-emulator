// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class ChargingSchedule(
    val id: Int,
    val chargingRateUnit: ChargingRateUnitEnum,
    val chargingSchedulePeriod: List<ChargingSchedulePeriod>,
    val limitAtSoC: LimitAtSoC? = null,
    /** Starting point of an absolute schedule or recurring schedule. */
    val startSchedule: ZonedDateTime? = null,
    /** Duration of the charging schedule in seconds. If the duration is left empty, the last period will continue indefinitely or until end of the transaction in case startSchedule is absent. */
    val duration: Int? = null,
    /** Minimum charging rate supported by the EV. The unit of measure is defined by the chargingRateUnit. This parameter is intended to be used by a local smart charging algorithm to optimize the power allocation for in the case a charging process is inefficient at lower charging rates. */
    val minChargingRate: Double? = null,
    /** *(2.1)* Power tolerance when following EVPowerProfile. */
    val powerTolerance: Double? = null,
    /** *(2.1)* Id of this element for referencing in a signature. */
    val signatureId: Int? = null,
    /** *(2.1)* Base64 encoded hash (SHA256 for ISO 15118-2, SHA512 for ISO 15118-20) of the EXI price schedule element. Used in signature. */
    val digestValue: String? = null,
    /** *(2.1)* Defaults to false. When true, disregard time zone offset in dateTime fields of _ChargingScheduleType_ and use unqualified local time at Charging Station instead. This allows the same `Absolute` or `Recurring` charging profile to be used in both summer and winter time. */
    val useLocalTime: Boolean? = null,
    /** *(2.1)* Defaults to 0. When _randomizedDelay_ not equals zero, then the start of each &lt;&lt;cmn_chargingscheduleperiodtype,ChargingSchedulePeriodType&gt;&gt; is delayed by a randomly chosen number of seconds between 0 and _randomizedDelay_. Only allowed for TxProfile and TxDefaultProfile. */
    val randomizedDelay: Int? = null,
    val salesTariff: SalesTariff? = null,
    val absolutePriceSchedule: AbsolutePriceSchedule? = null,
    val priceLevelSchedule: PriceLevelSchedule? = null,
    val customData: CustomData? = null,
)
