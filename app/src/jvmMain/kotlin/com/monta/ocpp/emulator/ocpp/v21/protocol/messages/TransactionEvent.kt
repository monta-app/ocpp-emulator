// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CostDetails
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.EVSE
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdToken
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdTokenInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MessageContent
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MeterValue
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.PreconditioningStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Transaction
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TransactionEventEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TransactionLimit
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TriggerReasonEnum
import java.time.ZonedDateTime

object TransactionEventFeature : Feature {
    override val name: String = "TransactionEvent"
    override val requestType: Class<out OcppRequest> = TransactionEventRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = TransactionEventResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class TransactionEventRequest(
    val eventType: TransactionEventEnum,
    /** The date and time at which this transaction event occurred. */
    val timestamp: ZonedDateTime,
    val triggerReason: TriggerReasonEnum,
    /** Incremental sequence number, helps with determining if all messages of a transaction have been received. */
    val seqNo: Int,
    val transactionInfo: Transaction,
    val costDetails: CostDetails? = null,
    val meterValue: List<MeterValue>? = null,
    /** Indication that this transaction event happened when the Charging Station was offline. Default = false, meaning: the event occurred when the Charging Station was online. */
    val offline: Boolean? = null,
    /** If the Charging Station is able to report the number of phases used, then it SHALL provide it. When omitted the CSMS may be able to determine the number of phases used as follows: + 1: The numberPhases in the currently used ChargingSchedule. + 2: The number of phases provided via device management. */
    val numberOfPhasesUsed: Int? = null,
    /** The maximum current of the connected cable in Ampere (A). */
    val cableMaxCurrent: Int? = null,
    /** This contains the Id of the reservation that terminates as a result of this transaction. */
    val reservationId: Int? = null,
    val preconditioningStatus: PreconditioningStatusEnum? = null,
    /** *(2.1)* True when EVSE electronics are in sleep mode for this transaction. Default value (when absent) is false. */
    val evseSleep: Boolean? = null,
    val evse: EVSE? = null,
    val idToken: IdToken? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class TransactionEventResponse(
    /** When _eventType_ of TransactionEventRequest is Updated, then this value contains the running cost. When _eventType_ of TransactionEventRequest is Ended, then this contains the final total cost of this transaction, including taxes, in the currency configured with the Configuration Variable: Currency. Absence of this value does not imply that the transaction was free. To indicate a free transaction, the CSMS SHALL send a value of 0.00. */
    val totalCost: Double? = null,
    /** Priority from a business point of view. Default priority is 0, The range is from -9 to 9. Higher values indicate a higher priority. The chargingPriority in &lt;&lt;transactioneventresponse,TransactionEventResponse&gt;&gt; is temporarily, so it may not be set in the &lt;&lt;cmn_idtokeninfotype,IdTokenInfoType&gt;&gt; afterwards. Also the chargingPriority in &lt;&lt;transactioneventresponse,TransactionEventResponse&gt;&gt; has a higher priority than the one in &lt;&lt;cmn_idtokeninfotype,IdTokenInfoType&gt;&gt;. */
    val chargingPriority: Int? = null,
    val idTokenInfo: IdTokenInfo? = null,
    val transactionLimit: TransactionLimit? = null,
    val updatedPersonalMessage: MessageContent? = null,
    val updatedPersonalMessageExtra: List<MessageContent>? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
