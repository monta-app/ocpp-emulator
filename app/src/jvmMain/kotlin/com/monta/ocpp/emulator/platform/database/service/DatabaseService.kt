package com.monta.ocpp.emulator.platform.database.service

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepoint.core.entity.PreviousMessagesTable
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.chargepoint.txdefault.entity.TxDefault
import com.monta.ocpp.emulator.platform.config.entity.AppConfigTable
import com.monta.ocpp.emulator.platform.database.service.DatabaseInitiator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

const val DEFAULT_DATABASE_NAME = "app.db"

class DatabaseService(
    databaseName: String = DEFAULT_DATABASE_NAME,
) {

    private val logger = KotlinLogging.logger {}
    private val database = DatabaseInitiator(databaseName).database

    fun connect() {
        try {
            transaction {
                val tables = arrayOf(
                    AppConfigTable,
                    ChargePointTable,
                    ChargePointConnectorTable,
                    ChargePointTransaction,
                    TxDefault,
                    PreviousMessagesTable,
                )
                SchemaUtils.create(*tables)
                SchemaUtils.addMissingColumnsStatements(*tables).forEach { statement ->
                    exec(statement)
                }
            }
        } catch (exception: Exception) {
            logger.error(exception) { "database error" }
            throw exception
        }
    }
}
