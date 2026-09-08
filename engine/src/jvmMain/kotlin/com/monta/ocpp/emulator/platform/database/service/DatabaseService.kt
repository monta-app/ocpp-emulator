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
import javax.inject.Singleton

@Singleton
class DatabaseService {

    companion object {
        /**
         * Every table the app persists. Register new tables here — the schema is created from this
         * list on startup, and the test harness builds its throwaway database from the same list.
         */
        val tables = arrayOf(
            AppConfigTable,
            ChargePointTable,
            ChargePointConnectorTable,
            ChargePointTransaction,
            TxDefault,
            PreviousMessagesTable,
        )
    }

    private val logger = KotlinLogging.logger {}
    private val database = DatabaseInitiator("app.db").database

    fun connect() {
        try {
            transaction {
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
