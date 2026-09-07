// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateHashData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomerInformationStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdToken
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object CustomerInformationFeature : Feature {
    override val name: String = "CustomerInformation"
    override val requestType: Class<out OcppRequest> = CustomerInformationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = CustomerInformationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class CustomerInformationRequest(
    /** The Id of the request. */
    val requestId: Int,
    /** Flag indicating whether the Charging Station should return NotifyCustomerInformationRequest messages containing information about the customer referred to. */
    val report: Boolean,
    /** Flag indicating whether the Charging Station should clear all information about the customer referred to. */
    val clear: Boolean,
    val customerCertificate: CertificateHashData? = null,
    val idToken: IdToken? = null,
    /** A (e.g. vendor specific) identifier of the customer this request refers to. This field contains a custom identifier other than IdToken and Certificate. One of the possible identifiers (customerIdentifier, customerIdToken or customerCertificate) should be in the request message. */
    val customerIdentifier: String? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class CustomerInformationResponse(
    val status: CustomerInformationStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
