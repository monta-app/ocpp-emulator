package com.monta.ocpp.emulator.common

import com.monta.ocpp.emulator.chargepoint.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepoint.entity.PreviousMessagesTable
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.configuration.AppConfigTable
import com.monta.ocpp.emulator.database.DatabaseInitiator
import com.monta.ocpp.emulator.v16.data.entity.TxDefault
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.inject.Singleton

@Singleton
class DatabaseService {

    private val logger = KotlinLogging.logger {}
    private val database = DatabaseInitiator("app.db").database

    fun connect() {
        try {
            transaction {
                SchemaUtils.createMissingTablesAndColumns(
                    AppConfigTable,
                    ChargePointTable,
                    ChargePointConnectorTable,
                    ChargePointTransaction,
                    TxDefault,
                    PreviousMessagesTable
                )
            }
        } catch (exception: Exception) {
            logger.error(exception) { "database error" }
            throw exception
        }
    }
}
