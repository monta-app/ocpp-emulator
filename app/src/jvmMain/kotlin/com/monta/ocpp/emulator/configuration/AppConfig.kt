package com.monta.ocpp.emulator.configuration

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

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
