// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetCertificateStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.OCSPRequestData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetCertificateStatusFeature : Feature {
    override val name: String = "GetCertificateStatus"
    override val requestType: Class<out OcppRequest> = GetCertificateStatusRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetCertificateStatusResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetCertificateStatusRequest(
    val ocspRequestData: OCSPRequestData,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetCertificateStatusResponse(
    val status: GetCertificateStatusEnum,
    val statusInfo: StatusInfo? = null,
    /** *(2.1)* OCSPResponse class as defined in &lt;&lt;ref-ocpp_security_24, IETF RFC 6960&gt;&gt;. DER encoded (as defined in &lt;&lt;ref-ocpp_security_24, IETF RFC 6960&gt;&gt;), and then base64 encoded. MAY only be omitted when status is not Accepted. + The minimum supported length is 18000. If a longer _ocspResult_ is supported, then the supported length must be communicated in variable OCPPCommCtrlr.FieldLength[ "GetCertificateStatusResponse.ocspResult" ]. */
    val ocspResult: String? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
