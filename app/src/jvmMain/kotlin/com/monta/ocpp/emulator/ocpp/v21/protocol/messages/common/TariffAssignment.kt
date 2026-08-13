// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class TariffAssignment(
    /** Tariff id. */
    val tariffId: String,
    val tariffKind: TariffKindEnum,
    /** Date/time when this tariff become active. */
    val validFrom: ZonedDateTime? = null,
    val evseIds: List<Int>? = null,
    /** IdTokens related to tariff */
    val idTokens: List<String>? = null,
    val customData: CustomData? = null,
)
