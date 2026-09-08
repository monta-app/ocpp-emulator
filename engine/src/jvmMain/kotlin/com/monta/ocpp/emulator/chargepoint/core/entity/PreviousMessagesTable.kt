package com.monta.ocpp.emulator.chargepoint.core.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

// Table Definition
object PreviousMessagesTable : LongIdTable("previous_messages") {
    val messageType = varchar("messageType", 512).index()
    val message = text("message")
}

// DAO
class PreviousMessagesDAO(
    id: EntityID<Long>,
) : LongEntity(id) {

    companion object : LongEntityClass<PreviousMessagesDAO>(PreviousMessagesTable) {
        fun newInstance(
            messageType: String,
            message: String,
        ): PreviousMessagesDAO {
            return PreviousMessagesDAO.new {
                this.messageType = messageType
                this.message = message
            }
        }
    }

    var messageType by PreviousMessagesTable.messageType
    var message by PreviousMessagesTable.message
}
