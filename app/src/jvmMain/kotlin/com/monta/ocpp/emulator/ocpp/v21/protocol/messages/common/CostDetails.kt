// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class CostDetails(
    val totalCost: TotalCost,
    val totalUsage: TotalUsage,
    val chargingPeriods: List<ChargingPeriod>? = null,
    /** If set to true, then Charging Station has failed to calculate the cost. */
    val failureToCalculate: Boolean? = null,
    /** Optional human-readable reason text in case of failure to calculate. */
    val failureReason: String? = null,
    val customData: CustomData? = null,
)
