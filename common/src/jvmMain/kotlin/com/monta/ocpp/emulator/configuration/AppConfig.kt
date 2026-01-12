package com.monta.ocpp.emulator.configuration

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

// Table Definition
object AppConfigTable : LongIdTable("configuration") {
    val key = varchar("key", 512).uniqueIndex()
    val value = text("value").nullable()
}

// DAO
class AppConfigDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AppConfigDAO>(AppConfigTable) {
        fun newInstance(
            key: String,
            value: String?,
        ): AppConfigDAO {
            return AppConfigDAO.new {
                this.key = key
                this.value = value
            }
        }
    }

    var key by AppConfigTable.key
    var value by AppConfigTable.value
}
