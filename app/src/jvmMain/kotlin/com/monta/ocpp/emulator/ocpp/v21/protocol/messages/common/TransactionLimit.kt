// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TransactionLimit(
    /** Maximum allowed cost of transaction in currency of tariff. */
    val maxCost: Double? = null,
    /** Maximum allowed energy in Wh to charge in transaction. */
    val maxEnergy: Double? = null,
    /** Maximum duration of transaction in seconds from start to end. */
    val maxTime: Int? = null,
    /** Maximum State of Charge of EV in percentage. */
    val maxSoC: Int? = null,
    val customData: CustomData? = null,
)
