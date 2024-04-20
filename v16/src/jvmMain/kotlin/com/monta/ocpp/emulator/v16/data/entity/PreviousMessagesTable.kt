package com.monta.ocpp.emulator.v16.data.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

// Table Definition
object PreviousMessagesTable : LongIdTable("previous_messages") {
    val messageType = varchar("messageType", 512).index()
    val message = text("message")
}

// DAO
class PreviousMessagesDAO(
    id: EntityID<Long>
) : LongEntity(id) {

    companion object : LongEntityClass<PreviousMessagesDAO>(PreviousMessagesTable) {
        fun newInstance(
            messageType: String,
            message: String
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
