// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Address(
    /** Name of person/company */
    val name: String,
    /** Address line 1 */
    val address1: String,
    /** City */
    val city: String,
    /** Country name */
    val country: String,
    /** Address line 2 */
    val address2: String? = null,
    /** Postal code */
    val postalCode: String? = null,
    val customData: CustomData? = null,
)
