// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class CertificateStatus(
    val certificateHashData: CertificateHashData,
    val source: CertificateStatusSourceEnum,
    val status: CertificateStatusEnum,
    val nextUpdate: ZonedDateTime,
    val customData: CustomData? = null,
)
