// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class IdToken(
    /** *(2.1)* IdToken is case insensitive. Might hold the hidden id of an RFID tag, but can for example also contain a UUID. */
    val idToken: String,
    /** *(2.1)* Enumeration of possible idToken types. Values defined in Appendix as IdTokenEnumStringType. */
    val type: String,
    val additionalInfo: List<AdditionalInfo>? = null,
    val customData: CustomData? = null,
)
