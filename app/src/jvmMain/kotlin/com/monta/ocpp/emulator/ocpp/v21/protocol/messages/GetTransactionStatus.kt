// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object GetTransactionStatusFeature : Feature {
    override val name: String = "GetTransactionStatus"
    override val requestType: Class<out OcppRequest> = GetTransactionStatusRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetTransactionStatusResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetTransactionStatusRequest(
    /** The Id of the transaction for which the status is requested. */
    val transactionId: String? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetTransactionStatusResponse(
    /** Whether there are still message to be delivered. */
    val messagesInQueue: Boolean,
    /** Whether the transaction is still ongoing. */
    val ongoingIndicator: Boolean? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
