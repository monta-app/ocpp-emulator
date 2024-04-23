package com.monta.ocpp.emulator.common

import com.monta.ocpp.emulator.chargepoint.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepoint.entity.PreviousMessagesTable
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.configuration.AppConfigTable
import com.monta.ocpp.emulator.database.DatabaseInitiator
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
