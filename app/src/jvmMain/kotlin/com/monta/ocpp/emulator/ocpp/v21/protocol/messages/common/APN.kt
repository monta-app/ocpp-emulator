// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class APN(
    /** The Access Point Name as an URL. */
    val apn: String,
    val apnAuthentication: APNAuthenticationEnum,
    /** APN username. */
    val apnUserName: String? = null,
    /** *(2.1)* APN Password. */
    val apnPassword: String? = null,
    /** SIM card pin code. */
    val simPin: Int? = null,
    /** Preferred network, written as MCC and MNC concatenated. See note. */
    val preferredNetwork: String? = null,
    /** Default: false. Use only the preferred Network, do not dial in when not available. See Note. */
    val useOnlyPreferredNetwork: Boolean? = null,
    val customData: CustomData? = null,
)
