package com.monta.ocpp.emulator.chargepoint.transaction.repository

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChargePointTransactionRepositoryTest {

    private val repository = ChargePointTransactionRepository()

    @BeforeEach
    fun setup() {
        // maximumPoolSize = 1 keeps a single connection alive so the in-memory DB survives across transaction { } blocks.
        val dataSource = HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = "jdbc:sqlite::memory:"
                username = "sa"
                password = ""
                poolName = "ChargePointTransactionRepositoryTest"
                maximumPoolSize = 1
            },
        )
        Database.connect(dataSource)
        transaction {
            SchemaUtils.create(ChargePointTable, ChargePointConnectorTable, ChargePointTransaction)
        }
    }

    // Reproduces the "RemoteStopTransaction keeps getting Rejected" bug: a CSMS-reused externalId used to resolve to a stale, already-ended transaction.
    @Test
    fun `getByExternalId ignores a completed transaction that reused the same externalId`() {
        val (chargePoint, connector) = createChargePointWithConnector()

        // A previous, completed session used external id 1001 and was stopped.
        transaction {
            ChargePointTransactionDAO.newInstance(
                chargePoint = chargePoint,
                chargePointConnector = connector,
                externalId = 1001,
                idTag = "OLD-TAG",
                endTime = Instant.now(),
                endReason = Reason.Remote,
            )
        }

        // The CSMS test backend was reset and reused id 1001 for a brand-new, still-open charge.
        val liveTransactionId = transaction {
            ChargePointTransactionDAO.newInstance(
                chargePoint = chargePoint,
                chargePointConnector = connector,
                externalId = 1001,
                idTag = "NEW-TAG",
            ).idValue
        }

        val found = transaction { repository.getByExternalId(1001) }

        assertEquals(liveTransactionId, found?.idValue)
        assertNull(found?.endTime)
    }

    @Test
    fun `getByExternalId returns null when no open transaction has that externalId`() {
        val (chargePoint, connector) = createChargePointWithConnector()

        transaction {
            ChargePointTransactionDAO.newInstance(
                chargePoint = chargePoint,
                chargePointConnector = connector,
                externalId = 1001,
                idTag = "OLD-TAG",
                endTime = Instant.now(),
                endReason = Reason.Remote,
            )
        }

        assertNull(transaction { repository.getByExternalId(1001) })
    }

    private fun createChargePointWithConnector(): Pair<ChargePointDAO, ChargePointConnectorDAO> {
        return transaction {
            val chargePoint = ChargePointDAO.newInstance(
                name = "CP1",
                identity = "CP1",
                password = null,
                ocppUrl = "ws://localhost",
                apiUrl = "http://localhost",
                firmware = "1.0",
                maxKw = 11.0,
            )
            val connector = ChargePointConnectorDAO.newInstance(
                chargePointId = chargePoint.idValue,
                chargePointIdentity = chargePoint.identity,
                position = 1,
                status = ChargePointStatus.Available,
                errorCode = ChargePointErrorCode.NoError,
                maxKw = 11.0,
            )
            chargePoint to connector
        }
    }
}
