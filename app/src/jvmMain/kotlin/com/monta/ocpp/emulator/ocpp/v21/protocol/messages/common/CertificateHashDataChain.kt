// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class CertificateHashDataChain(
    val certificateHashData: CertificateHashData,
    val certificateType: GetCertificateIdUseEnum,
    val childCertificateHashData: List<CertificateHashData>? = null,
    val customData: CustomData? = null,
)
