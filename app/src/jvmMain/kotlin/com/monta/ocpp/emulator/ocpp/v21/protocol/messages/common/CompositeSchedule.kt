// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class CompositeSchedule(
    val evseId: Int,
    val duration: Int,
    val scheduleStart: ZonedDateTime,
    val chargingRateUnit: ChargingRateUnitEnum,
    val chargingSchedulePeriod: List<ChargingSchedulePeriod>,
    val customData: CustomData? = null,
)
