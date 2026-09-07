// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Component(
    /** Name of the component. Name should be taken from the list of standardized component names whenever possible. Case Insensitive. strongly advised to use Camel Case. */
    val name: String,
    val evse: EVSE? = null,
    /** Name of instance in case the component exists as multiple instances. Case Insensitive. strongly advised to use Camel Case. */
    val instance: String? = null,
    val customData: CustomData? = null,
)
