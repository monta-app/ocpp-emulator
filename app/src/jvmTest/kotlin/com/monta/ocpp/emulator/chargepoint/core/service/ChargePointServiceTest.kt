package com.monta.ocpp.emulator.chargepoint.core.service

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.testsupport.DatabaseTest
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tier 2 — the service, the repository and the SQLite mapping running for real against a throwaway
 * database. Nothing is mocked; these services are transaction wrappers, so a mocked repository
 * would leave nothing under test.
 *
 * The case that earns this harness is connector reconciliation on [ChargePointService.upsert]:
 * lowering the connector count deletes rows, and deletions are worth a test that can actually fail.
 */
class ChargePointServiceTest : DatabaseTest() {

    private val service = ChargePointService(ChargePointRepository())

    @Test
    fun `creates a charge point with the requested connectors`() {
        val chargePoint = upsert(identity = "MEM_001", connectorCount = 2)

        transaction {
            assertEquals("MEM_001", chargePoint.identity)
            assertEquals(listOf(1, 2), chargePoint.connectors.map { connector -> connector.position }.sorted())
        }
    }

    @Test
    fun `matches an existing charge point on identity and updates it in place`() {
        val created = upsert(identity = "MEM_001", name = "First", connectorCount = 1)
        val updated = upsert(identity = "MEM_001", name = "Second", connectorCount = 1)

        transaction {
            assertEquals(created.id, updated.id)
            assertEquals("Second", updated.name)
            assertEquals(1, ChargePointDAO.all().count())
        }
    }

    @Test
    fun `adds connectors when the connector count grows and keeps the existing ones`() {
        val chargePoint = upsert(identity = "MEM_001", connectorCount = 1)
        val originalConnectorId = transaction { chargePoint.connectors.first().id }

        upsert(identity = "MEM_001", connectorCount = 3)

        transaction {
            assertEquals(listOf(1, 2, 3), chargePoint.connectors.map { connector -> connector.position }.sorted())
            assertNotNull(ChargePointConnectorDAO.findById(originalConnectorId))
        }
    }

    @Test
    fun `deletes surplus connectors and their transactions when the connector count shrinks`() {
        val chargePoint = upsert(identity = "MEM_001", connectorCount = 2)

        transaction {
            val secondConnector = chargePoint.connectors.first { connector -> connector.position == 2 }
            ChargePointTransactionDAO.newInstance(
                chargePoint = chargePoint,
                chargePointConnector = secondConnector,
                externalId = 4321,
                idTag = "TAG",
            )
        }

        upsert(identity = "MEM_001", connectorCount = 1)

        transaction {
            assertEquals(listOf(1), chargePoint.connectors.map { connector -> connector.position })
            assertEquals(
                0,
                ChargePointTransactionDAO.all().count(),
                "the surplus connector's transactions must go with it",
            )
        }
    }

    @Test
    fun `keeps the transactions belonging to connectors that survive`() {
        val chargePoint = upsert(identity = "MEM_001", connectorCount = 2)

        transaction {
            val firstConnector = chargePoint.connectors.first { connector -> connector.position == 1 }
            ChargePointTransactionDAO.newInstance(
                chargePoint = chargePoint,
                chargePointConnector = firstConnector,
                externalId = 1234,
                idTag = "TAG",
            )
        }

        upsert(identity = "MEM_001", connectorCount = 1)

        transaction {
            assertEquals(1, ChargePointTransactionDAO.all().count())
        }
    }

    @Test
    fun `is idempotent for an identity that is not already normalised`() {
        val created = upsert(identity = "  mem_001  ", connectorCount = 1)
        val updated = upsert(identity = "mem_001", connectorCount = 1)

        transaction {
            assertEquals(created.id, updated.id, "the second upsert must find the row the first created")
            assertEquals(1, ChargePointDAO.all().count())
            assertEquals("MEM_001", ChargePointDAO.all().single().identity)
        }
    }

    @Test
    fun `finds a charge point by an identity that is not already normalised`() {
        val created = upsert(identity = "MEM_001", connectorCount = 1)

        transaction {
            assertEquals(created.id, service.getByIdentity("mem_001").id)
        }
    }

    private fun upsert(
        identity: String,
        connectorCount: Int,
        name: String = "Emulator",
    ): ChargePointDAO {
        return service.upsert(
            name = name,
            identity = identity,
            password = null,
            ocppUrl = "wss://example.invalid/ocpp",
            apiUrl = "https://example.invalid",
            firmware = "1.0.0",
            maxKw = 22.0,
            connectorCount = connectorCount,
            meterType = MeterType.OCPP,
        )
    }
}
