// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Variable(
    /** Name of the variable. Name should be taken from the list of standardized variable names whenever possible. Case Insensitive. strongly advised to use Camel Case. */
    val name: String,
    /** Name of instance in case the variable exists as multiple instances. Case Insensitive. strongly advised to use Camel Case. */
    val instance: String? = null,
    val customData: CustomData? = null,
)
