package com.monta.ocpp.emulator.chargepoint.core.service

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Tier 2 — the service, the repository and the SQLite mapping running for real against a throwaway
 * database. Nothing is mocked; these services are transaction wrappers, so a mocked repository
 * would leave nothing under test.
 *
 * The case that earns this harness is connector reconciliation on [ChargePointService.upsert]:
 * lowering the connector count deletes rows, and deletions are worth a test that can actually fail.
 */
class ChargePointServiceTest : DatabaseSpec({

    val service = ChargePointService(ChargePointRepository())

    fun upsert(
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

    describe("upsert") {

        it("creates a charge point with the requested connectors") {
            val chargePoint = upsert(identity = "MEM_001", connectorCount = 2)

            transaction {
                val connectorPositions = chargePoint.connectors.map { connector -> connector.position }.sorted()

                chargePoint.identity shouldBe "MEM_001"
                connectorPositions shouldBe listOf(1, 2)
            }
        }

        it("matches an existing charge point on identity and updates it in place") {
            val created = upsert(identity = "MEM_001", name = "First", connectorCount = 1)
            val updated = upsert(identity = "MEM_001", name = "Second", connectorCount = 1)

            transaction {
                updated.id shouldBe created.id
                updated.name shouldBe "Second"
                ChargePointDAO.all().count() shouldBe 1
            }
        }

        it("is idempotent for an identity that is not already normalised") {
            val created = upsert(identity = "  mem_001  ", connectorCount = 1)
            val updated = upsert(identity = "mem_001", connectorCount = 1)

            transaction {
                val storedChargePoints = ChargePointDAO.all()

                updated.id shouldBe created.id
                storedChargePoints.count() shouldBe 1
                storedChargePoints.single().identity shouldBe "MEM_001"
            }
        }

        describe("connector reconciliation") {

            it("adds connectors when the count grows and keeps the existing ones") {
                val chargePoint = upsert(identity = "MEM_001", connectorCount = 1)
                val originalConnectorId = transaction { chargePoint.connectors.first().id }

                upsert(identity = "MEM_001", connectorCount = 3)

                transaction {
                    val connectorPositions = chargePoint.connectors.map { connector -> connector.position }.sorted()

                    connectorPositions shouldBe listOf(1, 2, 3)
                    ChargePointConnectorDAO.findById(originalConnectorId).shouldNotBeNull()
                }
            }

            it("deletes surplus connectors and their transactions when the count shrinks") {
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
                    val connectorPositions = chargePoint.connectors.map { connector -> connector.position }

                    connectorPositions shouldBe listOf(1)
                    ChargePointTransactionDAO.all().count() shouldBe 0
                }
            }

            it("keeps the transactions belonging to connectors that survive") {
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
                    ChargePointTransactionDAO.all().count() shouldBe 1
                }
            }
        }
    }

    describe("getByIdentity") {

        it("finds a charge point by an identity that is not already normalised") {
            val created = upsert(identity = "MEM_001", connectorCount = 1)

            transaction {
                val found = service.getByIdentity("mem_001")

                found.id shouldBe created.id
            }
        }
    }
})
