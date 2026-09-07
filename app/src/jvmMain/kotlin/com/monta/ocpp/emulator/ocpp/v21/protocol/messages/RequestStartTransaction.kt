// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingProfile
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdToken
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.RequestStartStopStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object RequestStartTransactionFeature : Feature {
    override val name: String = "RequestStartTransaction"
    override val requestType: Class<out OcppRequest> = RequestStartTransactionRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = RequestStartTransactionResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class RequestStartTransactionRequest(
    val idToken: IdToken,
    /** Id given by the server to this start request. The Charging Station will return this in the &lt;&lt;transactioneventrequest, TransactionEventRequest&gt;&gt;, letting the server know which transaction was started for this request. Use to start a transaction. */
    val remoteStartId: Int,
    /** Number of the EVSE on which to start the transaction. EvseId SHALL be &gt; 0 */
    val evseId: Int? = null,
    val groupIdToken: IdToken? = null,
    val chargingProfile: ChargingProfile? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class RequestStartTransactionResponse(
    val status: RequestStartStopStatusEnum,
    val statusInfo: StatusInfo? = null,
    /** When the transaction was already started by the Charging Station before the RequestStartTransactionRequest was received, for example: cable plugged in first. This contains the transactionId of the already started transaction. */
    val transactionId: String? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
