// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateActionEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Iso15118EVCertificateStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object Get15118EVCertificateFeature : Feature {
    override val name: String = "Get15118EVCertificate"
    override val requestType: Class<out OcppRequest> = Get15118EVCertificateRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = Get15118EVCertificateResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class Get15118EVCertificateRequest(
    /** Schema version currently used for the 15118 session between EV and Charging Station. Needed for parsing of the EXI stream by the CSMS. */
    val iso15118SchemaVersion: String,
    val action: CertificateActionEnum,
    /** *(2.1)* Raw CertificateInstallationReq request from EV, Base64 encoded. + Extended to support ISO 15118-20 certificates. The minimum supported length is 11000. If a longer _exiRequest_ is supported, then the supported length must be communicated in variable OCPPCommCtrlr.FieldLength[ "Get15118EVCertificateRequest.exiRequest" ]. */
    val exiRequest: String,
    /** *(2.1)* Absent during ISO 15118-2 session. Required during ISO 15118-20 session. + Maximum number of contracts that EV wants to install. */
    val maximumContractCertificateChains: Int? = null,
    /** *(2.1)* Absent during ISO 15118-2 session. Optional during ISO 15118-20 session. List of EMAIDs for which contract certificates must be requested first, in case there are more certificates than allowed by _maximumContractCertificateChains_. */
    val prioritizedEMAIDs: List<String>? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class Get15118EVCertificateResponse(
    val status: Iso15118EVCertificateStatusEnum,
    /** *(2/1)* Raw CertificateInstallationRes response for the EV, Base64 encoded. + Extended to support ISO 15118-20 certificates. The minimum supported length is 17000. If a longer _exiResponse_ is supported, then the supported length must be communicated in variable OCPPCommCtrlr.FieldLength[ "Get15118EVCertificateResponse.exiResponse" ]. */
    val exiResponse: String,
    val statusInfo: StatusInfo? = null,
    /** *(2.1)* Number of contracts that can be retrieved with additional requests. */
    val remainingContracts: Int? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
