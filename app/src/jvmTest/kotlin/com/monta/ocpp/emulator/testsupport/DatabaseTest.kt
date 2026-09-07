package com.monta.ocpp.emulator.testsupport

import com.monta.ocpp.emulator.platform.database.service.DatabaseService
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteIfExists

/**
 * Base class for tests that exercise real persistence.
 *
 * Each test gets its own throwaway SQLite database, built from [DatabaseService.tables] so the test
 * schema cannot drift from the one the app creates on startup. Nothing is mocked: the service, the
 * repository, the Exposed mapping and the SQLite dialect all run for real, which is the point —
 * these are the layers where a mock would assert nothing.
 *
 * SQLite rather than H2 on purpose. The app ships a pinned SQLite driver and the failures worth
 * catching here are dialect- and mapping-level; H2 would hide exactly those.
 */
abstract class DatabaseTest {

    private lateinit var databaseFile: Path
    private lateinit var database: Database

    @BeforeEach
    fun connectDatabase() {
        databaseFile = Files.createTempFile("ocpp-emulator-test-", ".db")
        database = Database.connect(
            url = "jdbc:sqlite:${databaseFile.absolutePathString()}",
            driver = "org.sqlite.JDBC",
        )
        transaction(database) {
            SchemaUtils.create(*DatabaseService.tables)
        }
    }

    @AfterEach
    fun disconnectDatabase() {
        TransactionManager.closeAndUnregister(database)
        databaseFile.deleteIfExists()
    }
}
