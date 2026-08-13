// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class CertificateStatusRequestInfo(
    val certificateHashData: CertificateHashData,
    val source: CertificateStatusSourceEnum,
    /** URL(s) of _source_. */
    val urls: List<String>,
    val customData: CustomData? = null,
)
