// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateHashData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateSigningUseEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object SignCertificateFeature : Feature {
    override val name: String = "SignCertificate"
    override val requestType: Class<out OcppRequest> = SignCertificateRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = SignCertificateResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SignCertificateRequest(
    /** The Charging Station SHALL send the public key in form of a Certificate Signing Request (CSR) as described in RFC 2986 [22] and then PEM encoded, using the &lt;&lt;signcertificaterequest,SignCertificateRequest&gt;&gt; message. */
    val csr: String,
    val certificateType: CertificateSigningUseEnum? = null,
    val hashRootCertificate: CertificateHashData? = null,
    /** *(2.1)* RequestId to match this message with the CertificateSignedRequest. */
    val requestId: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SignCertificateResponse(
    val status: GenericStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
