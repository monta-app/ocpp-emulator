// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.AuthorizeCertificateStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.EnergyTransferModeEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdToken
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdTokenInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.OCSPRequestData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Tariff

object AuthorizeFeature : Feature {
    override val name: String = "Authorize"
    override val requestType: Class<out OcppRequest> = AuthorizeRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = AuthorizeResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class AuthorizeRequest(
    val idToken: IdToken,
    /** *(2.1)* The X.509 certificate chain presented by EV and encoded in PEM format. Order of certificates in chain is from leaf up to (but excluding) root certificate. + Only needed in case of central contract validation when Charging Station cannot validate the contract certificate. */
    val certificate: String? = null,
    val iso15118CertificateHashData: List<OCSPRequestData>? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class AuthorizeResponse(
    val idTokenInfo: IdTokenInfo,
    val certificateStatus: AuthorizeCertificateStatusEnum? = null,
    /** *(2.1)* List of allowed energy transfer modes the EV can choose from. If omitted this defaults to charging only. */
    val allowedEnergyTransfer: List<EnergyTransferModeEnum>? = null,
    val tariff: Tariff? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
