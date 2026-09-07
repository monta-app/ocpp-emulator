// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.transactions

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTransactionStatusFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTransactionStatusRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTransactionStatusResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TransactionEventFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TransactionEventRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.TransactionEventResponse

val transactionsFeatures = listOf(
    TransactionEventFeature,
    GetTransactionStatusFeature,
)

class TransactionsClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = transactionsFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is GetTransactionStatusRequest -> listener.getTransactionStatus(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun getTransactionStatus(
            ocppSessionInfo: OcppSession.Info,
            request: GetTransactionStatusRequest,
        ): GetTransactionStatusResponse
    }

    interface Sender {
        suspend fun transactionEvent(
            request: TransactionEventRequest,
        ): TransactionEventResponse
    }
}
