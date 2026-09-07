package com.monta.ocpp.emulator.testsupport

import com.monta.ocpp.emulator.platform.database.service.DatabaseService
import io.kotest.core.spec.Spec
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteIfExists

/**
 * Gives every leaf test in this spec its own empty SQLite database.
 *
 * The schema is built from [DatabaseService.tables] so it cannot drift from the one the app creates
 * on startup. Nothing is mocked: the service, the repository, the Exposed mapping and the SQLite
 * dialect all run for real, which is the point — these are the layers where a mock would assert
 * nothing.
 *
 * SQLite rather than H2 on purpose. The app ships a pinned SQLite driver and the failures worth
 * catching here are dialect- and mapping-level; H2 would hide exactly those.
 *
 * Call it once at the top of the spec body:
 * ```
 * class ExampleTest : DescribeSpec({
 *     useThrowawayDatabase()
 *     describe("something") { ... }
 * })
 * ```
 */
fun Spec.useThrowawayDatabase() {
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
