package com.monta.ocpp.emulator.control.service

import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v16.service.ChargePointManager
import com.monta.ocpp.emulator.platform.database.service.DatabaseService
import com.monta.ocpp.emulator.platform.util.appRoot
import java.io.File
import java.util.UUID

/**
 * Shared, JVM-wide fixture for the `control/` test suite. A real SQLite-backed
 * [ChargePointService]/[ConnectionManager]/[ChargePointManager] graph, built the same way
 * [com.monta.ocpp.emulator.MontaKoinModule] wires it, but constructed directly instead of
 * through Koin (this project has no test mocking library, and the graph is cheap enough to
 * assemble by hand).
 *
 * This is a Kotlin `object` - lazily initialized on first reference, and only ever
 * initialized once - specifically so every test class that needs the control command graph
 * shares *one* [DatabaseService.connect] call. Exposed's no-arg `transaction { }` operates
 * against whichever [org.jetbrains.exposed.v1.jdbc.Database] was most recently connected, so
 * if two test classes each called `DatabaseService(...).connect()` independently, whichever
 * ran second would silently become "the" database for every `transaction { }` call already
 * in flight from the first, including from tests that had already seeded data expecting to
 * read it back.
 *
 * Deliberately does *not* exercise `chargePoint.connect`/`disconnect`/`connector.authorize` -
 * those need a live (or fake) OCPP websocket/CSMS, which is out of scope for a fast unit
 * test. Every command that only touches persisted state - including the ones that go through
 * [com.monta.ocpp.emulator.ocpp.v16.extension] (`setStatus`, `setConnectorCarState`,
 * `stopActiveTransactions`) - works fine here even without Koin/a websocket: those functions
 * catch-and-log the "no OCPP client available" failure internally rather than propagating it
 * (see `ocpp/v16/extension/OcppClientExtensions.kt#statusNotification`), and free-charging
 * auto-start (the one path that *would* propagate) is off by default
 * (`ChargePointConfiguration.freeCharging`).
 */
object ControlTestFixture {

    private val chargePointRepository = ChargePointRepository()

    val chargePointService = ChargePointService(chargePointRepository)
    val connectionManager = ConnectionManager(MessageInterceptor(chargePointService), chargePointRepository)
    val chargePointManager = ChargePointManager()
    val dispatcher = ControlCommandDispatcher(chargePointService, connectionManager, chargePointManager)

    init {
        val databaseName = "test-control-${UUID.randomUUID()}.db"
        val databaseFile = File(appRoot, databaseName)
        DatabaseService(databaseName = databaseName).connect()
        Runtime.getRuntime().addShutdownHook(Thread { databaseFile.delete() })
    }

    /**
     * Seeds a charge point with a unique, already-uppercase identity (matching
     * [ChargePointDAO.newInstance]'s own normalization, so callers can use the identity they
     * pass in to look the charge point back up without a case mismatch).
     */
    fun seedChargePoint(
        identity: String,
        connectorCount: Int = 0,
    ): ChargePointDAO {
        return chargePointService.upsert(
            name = identity,
            identity = identity.uppercase(),
            password = null,
            ocppUrl = "ws://localhost:19999/ocpp",
            apiUrl = "http://localhost:19999",
            firmware = "1.0",
            maxKw = 11.0,
            connectorCount = connectorCount,
            meterType = MeterType.OCPP,
        )
    }
}
