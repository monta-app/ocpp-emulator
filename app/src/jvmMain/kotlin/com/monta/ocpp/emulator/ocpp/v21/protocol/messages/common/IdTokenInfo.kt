// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class IdTokenInfo(
    val status: AuthorizationStatusEnum,
    /** Date and Time after which the token must be considered invalid. */
    val cacheExpiryDateTime: ZonedDateTime? = null,
    /** Priority from a business point of view. Default priority is 0, The range is from -9 to 9. Higher values indicate a higher priority. The chargingPriority in &lt;&lt;transactioneventresponse,TransactionEventResponse&gt;&gt; overrules this one. */
    val chargingPriority: Int? = null,
    val groupIdToken: IdToken? = null,
    /** Preferred user interface language of identifier user. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;. */
    val language1: String? = null,
    /** Second preferred user interface language of identifier user. Don’t use when language1 is omitted, has to be different from language1. Contains a language code as defined in &lt;&lt;ref-RFC5646,[RFC5646]&gt;&gt;. */
    val language2: String? = null,
    /** Only used when the IdToken is only valid for one or more specific EVSEs, not for the entire Charging Station. */
    val evseId: List<Int>? = null,
    val personalMessage: MessageContent? = null,
    val customData: CustomData? = null,
)
