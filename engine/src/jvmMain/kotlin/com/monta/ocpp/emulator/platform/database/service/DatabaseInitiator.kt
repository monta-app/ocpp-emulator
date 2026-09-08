package com.monta.ocpp.emulator.platform.database.service

import com.monta.ocpp.emulator.platform.util.appRoot
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.v1.jdbc.Database
import java.io.File
import javax.sql.DataSource

class DatabaseInitiator(
    private val databaseName: String,
) {

    private val logger = KotlinLogging.logger {}

    private val databasePath by lazy {
        "${appRoot.absolutePath}${File.separator}$databaseName"
    }

    private val dataSource: DataSource
    val database: Database

    init {
        try {
            logger.debug { "starting database $databasePath" }

            dataSource = HikariDataSource(
                HikariConfig().apply {
                    jdbcUrl = "jdbc:sqlite:$databasePath"
                    username = "sa"
                    password = ""
                    poolName = "SQLiteConnectionPool"
                    maximumPoolSize = 1
                },
            )
            database = Database.connect(dataSource)
        } catch (exception: Exception) {
            logger.error(exception) { "database error" }
            throw exception
        }
    }
}
