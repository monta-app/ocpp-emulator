// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TotalCost(
    /** Currency of the costs in ISO 4217 Code. */
    val currency: String,
    val typeOfCost: TariffCostEnum,
    val total: TotalPrice,
    val fixed: Price? = null,
    val energy: Price? = null,
    val chargingTime: Price? = null,
    val idleTime: Price? = null,
    val reservationTime: Price? = null,
    val reservationFixed: Price? = null,
    val customData: CustomData? = null,
)
