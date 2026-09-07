// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.InstallCertificateStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.InstallCertificateUseEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object InstallCertificateFeature : Feature {
    override val name: String = "InstallCertificate"
    override val requestType: Class<out OcppRequest> = InstallCertificateRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = InstallCertificateResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class InstallCertificateRequest(
    val certificateType: InstallCertificateUseEnum,
    /** A PEM encoded X.509 certificate. */
    val certificate: String,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class InstallCertificateResponse(
    val status: InstallCertificateStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
