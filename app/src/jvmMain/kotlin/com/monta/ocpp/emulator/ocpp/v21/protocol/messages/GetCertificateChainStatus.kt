// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateStatus
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateStatusRequestInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object GetCertificateChainStatusFeature : Feature {
    override val name: String = "GetCertificateChainStatus"
    override val requestType: Class<out OcppRequest> = GetCertificateChainStatusRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetCertificateChainStatusResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetCertificateChainStatusRequest(
    val certificateStatusRequests: List<CertificateStatusRequestInfo>,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetCertificateChainStatusResponse(
    val certificateStatus: List<CertificateStatus>,
    val customData: CustomData? = null,
) : OcppConfirmation
