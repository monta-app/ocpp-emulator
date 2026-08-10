package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.transactions.TransactionsClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTransactionStatusRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTransactionStatusResponse
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class TransactionsHandler : TransactionsClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()

    override suspend fun getTransactionStatus(
        ocppSessionInfo: OcppSession.Info,
        request: GetTransactionStatusRequest,
    ): GetTransactionStatusResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val ongoing = if (request.transactionId == null) {
            chargePoint.getActiveTransactions().isNotEmpty()
        } else {
            chargePoint.getActiveTransactions().any {
                it.ocppTransactionId == request.transactionId ||
                    it.externalId.toString() == request.transactionId
            }
        }
        return GetTransactionStatusResponse(
            messagesInQueue = false,
            ongoingIndicator = ongoing,
        )
    }
}
