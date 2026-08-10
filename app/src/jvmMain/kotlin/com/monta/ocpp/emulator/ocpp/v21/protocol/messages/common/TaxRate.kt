// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TaxRate(
    /** Type of this tax, e.g. "Federal ", "State", for information on receipt. */
    val type: String,
    /** Tax percentage */
    val tax: Double,
    /** Stack level for this type of tax. Default value, when absent, is 0. + _stack_ = 0: tax on net price; + _stack_ = 1: tax added on top of _stack_ 0; + _stack_ = 2: tax added on top of _stack_ 1, etc. */
    val stack: Int? = null,
    val customData: CustomData? = null,
)
