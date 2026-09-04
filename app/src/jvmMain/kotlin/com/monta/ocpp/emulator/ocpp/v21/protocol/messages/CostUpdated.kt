// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object CostUpdatedFeature : Feature {
    override val name: String = "CostUpdated"
    override val requestType: Class<out OcppRequest> = CostUpdatedRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = CostUpdatedResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class CostUpdatedRequest(
    /** Current total cost, based on the information known by the CSMS, of the transaction including taxes. In the currency configured with the configuration Variable: [&lt;&lt;configkey-currency, Currency&gt;&gt;] */
    val totalCost: Double,
    /** Transaction Id of the transaction the current cost are asked for. */
    val transactionId: String,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class CostUpdatedResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
