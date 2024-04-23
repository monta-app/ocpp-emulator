package com.monta.ocpp.emulator.chargepoint.service

import com.monta.ocpp.emulator.chargepoint.entity.PreviousMessagesDAO
import com.monta.ocpp.emulator.chargepoint.entity.PreviousMessagesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class PreviousMessagesService {
    fun getAllOfMessageType(messageType: String): List<PreviousMessagesDAO> {
        return transaction {
            PreviousMessagesDAO
                .find { PreviousMessagesTable.messageType eq messageType }
                .toList()
                .reversed()
        }
    }

    fun insertNewMessage(
        messageType: String,
        message: String
    ) {
        // Delete previous identical messages - this makes sure the last used message is on top:
        transaction {
            PreviousMessagesTable.deleteWhere {
                (PreviousMessagesTable.message eq message) and (PreviousMessagesTable.messageType eq messageType)
            }
        }

        transaction {
            PreviousMessagesDAO.newInstance(
                messageType = messageType,
                message = message.trim()
            )
        }
    }

    fun deleteMessage(id: Long) {
        transaction { PreviousMessagesTable.deleteWhere { PreviousMessagesTable.id eq id } }
    }
}
