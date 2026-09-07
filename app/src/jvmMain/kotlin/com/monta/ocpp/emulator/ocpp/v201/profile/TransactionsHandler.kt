package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.v201.blocks.transactions.GetTransactionStatusRequest
import com.monta.library.ocpp.v201.blocks.transactions.GetTransactionStatusResponse
import com.monta.library.ocpp.v201.blocks.transactions.TransactionsClientDispatcher
import com.monta.ocpp.emulator.chargepoint.transaction.service.ChargePointTransactionService
import javax.inject.Singleton

@Singleton
class TransactionsHandler(
    private val transactionService: ChargePointTransactionService,
) : TransactionsClientDispatcher.Listener {
    override suspend fun getTransactionStatus(
        request: GetTransactionStatusRequest,
    ): GetTransactionStatusResponse {
        val transactionId = request.transactionId
        val ongoing = if (transactionId != null) {
            transactionService.getByOcppTransactionId(transactionId)?.canStop()
        } else {
            null
        }
        return GetTransactionStatusResponse(
            ongoingIndicator = ongoing,
            messagesInQueue = false,
        )
    }
}
