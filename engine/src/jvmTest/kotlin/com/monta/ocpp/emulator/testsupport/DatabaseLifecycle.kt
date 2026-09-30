package com.monta.ocpp.emulator.testsupport

import com.monta.ocpp.emulator.platform.database.service.DatabaseService
import io.kotest.core.spec.style.DescribeSpec
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteIfExists

fun DescribeSpec.useThrowawayDatabase() {
    var databaseFile: Path? = null
    var database: Database? = null

    beforeEach {
        val file = Files.createTempFile("ocpp-emulator-test-", ".db")
        databaseFile = file
        database = Database.connect(
            url = "jdbc:sqlite:${file.absolutePathString()}",
            driver = "org.sqlite.JDBC",
        )
        transaction {
            SchemaUtils.create(*DatabaseService.tables)
        }
    }

    afterEach {
        database?.let { connectedDatabase -> TransactionManager.closeAndUnregister(connectedDatabase) }
        databaseFile?.deleteIfExists()
        database = null
        databaseFile = null
    }
}
