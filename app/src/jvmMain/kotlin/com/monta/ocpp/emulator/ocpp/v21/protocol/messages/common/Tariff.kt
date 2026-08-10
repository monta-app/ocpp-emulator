// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class Tariff(
    /** Unique id of tariff */
    val tariffId: String,
    /** Currency code according to ISO 4217 */
    val currency: String,
    val description: List<MessageContent>? = null,
    val energy: TariffEnergy? = null,
    /** Time when this tariff becomes active. When absent, it is immediately active. */
    val validFrom: ZonedDateTime? = null,
    val chargingTime: TariffTime? = null,
    val idleTime: TariffTime? = null,
    val fixedFee: TariffFixed? = null,
    val reservationTime: TariffTime? = null,
    val reservationFixed: TariffFixed? = null,
    val minCost: Price? = null,
    val maxCost: Price? = null,
    val customData: CustomData? = null,
)
