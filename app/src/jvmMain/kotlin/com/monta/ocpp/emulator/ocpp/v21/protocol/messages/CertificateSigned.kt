// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateSignedStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateSigningUseEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object CertificateSignedFeature : Feature {
    override val name: String = "CertificateSigned"
    override val requestType: Class<out OcppRequest> = CertificateSignedRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = CertificateSignedResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class CertificateSignedRequest(
    /** The signed PEM encoded X.509 certificate. This SHALL also contain the necessary sub CA certificates, when applicable. The order of the bundle follows the certificate chain, starting from the leaf certificate. The Configuration Variable &lt;&lt;configkey-max-certificate-chain-size,MaxCertificateChainSize&gt;&gt; can be used to limit the maximum size of this field. */
    val certificateChain: String,
    val certificateType: CertificateSigningUseEnum? = null,
    /** *(2.1)* RequestId to correlate this message with the SignCertificateRequest. */
    val requestId: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class CertificateSignedResponse(
    val status: CertificateSignedStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
