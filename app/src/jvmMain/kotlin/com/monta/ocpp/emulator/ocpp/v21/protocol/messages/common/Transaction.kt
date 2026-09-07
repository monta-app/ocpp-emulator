// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Transaction(
    /** This contains the Id of the transaction. */
    val transactionId: String,
    val chargingState: ChargingStateEnum? = null,
    /** Contains the total time that energy flowed from EVSE to EV during the transaction (in seconds). Note that timeSpentCharging is smaller or equal to the duration of the transaction. */
    val timeSpentCharging: Int? = null,
    val stoppedReason: ReasonEnum? = null,
    /** The ID given to remote start request (&lt;&lt;requeststarttransactionrequest, RequestStartTransactionRequest&gt;&gt;. This enables to CSMS to match the started transaction to the given start request. */
    val remoteStartId: Int? = null,
    val operationMode: OperationModeEnum? = null,
    /** *(2.1)* Id of tariff in use for transaction */
    val tariffId: String? = null,
    val transactionLimit: TransactionLimit? = null,
    val customData: CustomData? = null,
)
