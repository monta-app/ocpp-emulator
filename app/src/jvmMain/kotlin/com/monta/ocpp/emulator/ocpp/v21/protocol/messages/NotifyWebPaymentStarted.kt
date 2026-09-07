// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object NotifyWebPaymentStartedFeature : Feature {
    override val name: String = "NotifyWebPaymentStarted"
    override val requestType: Class<out OcppRequest> = NotifyWebPaymentStartedRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyWebPaymentStartedResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyWebPaymentStartedRequest(
    /** EVSE id for which transaction is requested. */
    val evseId: Int,
    /** Timeout value in seconds after which no result of web payment process (e.g. QR code scanning) is to be expected anymore. */
    val timeout: Int,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyWebPaymentStartedResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
