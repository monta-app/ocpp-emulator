// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class ChargingPeriod(
    /** Start timestamp of charging period. A period ends when the next period starts. The last period ends when the session ends. */
    val startPeriod: ZonedDateTime,
    val dimensions: List<CostDimension>? = null,
    /** Unique identifier of the Tariff that was used to calculate cost. If not provided, then cost was calculated by some other means. */
    val tariffId: String? = null,
    val customData: CustomData? = null,
)
