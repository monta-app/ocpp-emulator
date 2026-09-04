// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateHashDataChain
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetCertificateIdUseEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetInstalledCertificateStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetInstalledCertificateIdsFeature : Feature {
    override val name: String = "GetInstalledCertificateIds"
    override val requestType: Class<out OcppRequest> = GetInstalledCertificateIdsRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetInstalledCertificateIdsResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetInstalledCertificateIdsRequest(
    /** Indicates the type of certificates requested. When omitted, all certificate types are requested. */
    val certificateType: List<GetCertificateIdUseEnum>? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetInstalledCertificateIdsResponse(
    val status: GetInstalledCertificateStatusEnum,
    val statusInfo: StatusInfo? = null,
    val certificateHashDataChain: List<CertificateHashDataChain>? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
