package com.monta.ocpp.emulator.v201.features.database

import com.monta.ocpp.emulator.configuration.AppConfigTable
import com.monta.ocpp.emulator.database.DatabaseInitiator
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton(createdAtStart = true)
class DatabaseService {

    private val logger = KotlinLogging.logger {}
    private val database = DatabaseInitiator("app-v201.db").database

    init {
        try {
            transaction {
                SchemaUtils.createMissingTablesAndColumns(
                    AppConfigTable
                )
            }
        } catch (exception: Exception) {
            logger.error("database error", exception)
            throw exception
        }
    }
}
