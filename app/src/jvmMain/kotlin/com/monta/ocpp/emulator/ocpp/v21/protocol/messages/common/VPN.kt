// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class VPN(
    /** VPN Server Address */
    val server: String,
    /** VPN User */
    val user: String,
    /** *(2.1)* VPN Password. */
    val password: String,
    /** VPN shared secret. */
    val key: String,
    val type: VPNEnum,
    /** VPN group. */
    val group: String? = null,
    val customData: CustomData? = null,
)
