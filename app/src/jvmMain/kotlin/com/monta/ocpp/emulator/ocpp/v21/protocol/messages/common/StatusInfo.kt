// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class StatusInfo(
    /** A predefined code for the reason why the status is returned in this response. The string is case-insensitive. */
    val reasonCode: String,
    /** Additional text to provide detailed information. */
    val additionalInfo: String? = null,
    val customData: CustomData? = null,
)
