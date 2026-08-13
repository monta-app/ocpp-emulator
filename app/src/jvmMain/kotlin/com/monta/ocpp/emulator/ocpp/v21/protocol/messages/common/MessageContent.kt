// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class MessageContent(
    val format: MessageFormatEnum,
    /** *(2.1)* Required. Message contents. + Maximum length supported by Charging Station is given in OCPPCommCtrlr.FieldLength["MessageContentType.content"]. Maximum length defaults to 1024. */
    val content: String,
    /** Message language identifier. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;. */
    val language: String? = null,
    val customData: CustomData? = null,
)
