package com.monta.ocpp.emulator.v16.data

import com.monta.ocpp.emulator.configuration.AppConfigTable
import com.monta.ocpp.emulator.database.DatabaseInitiator
import com.monta.ocpp.emulator.v16.data.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.v16.data.entity.ChargePointTable
import com.monta.ocpp.emulator.v16.data.entity.ChargePointTransaction
import com.monta.ocpp.emulator.v16.data.entity.PreviousMessagesTable
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton(createdAtStart = true)
class DatabaseService {

    private val logger = KotlinLogging.logger {}
    private val database = DatabaseInitiator("app.db").database

    init {
        try {
            transaction {
                SchemaUtils.createMissingTablesAndColumns(
                    AppConfigTable,
                    ChargePointTable,
                    ChargePointConnectorTable,
                    ChargePointTransaction,
                    PreviousMessagesTable
                )
            }
        } catch (exception: Exception) {
            logger.error("database error", exception)
            throw exception
        }
    }
}
