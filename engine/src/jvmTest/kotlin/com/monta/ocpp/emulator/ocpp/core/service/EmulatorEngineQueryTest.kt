package com.monta.ocpp.emulator.ocpp.core.service

import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.repository.ChargePointConnectorRepository
import com.monta.ocpp.emulator.chargepoint.connector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.exception.ChargePointNotFoundException
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.model.OcppVersion
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.core.service.PreviousMessagesService
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v16.service.ChargePointManager
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Tier 2 — exercises the DAO → DTO projection and the pure-DB commands of [DefaultEmulatorEngine]
 * against a throwaway SQLite database. Nothing is mocked: the services, repositories, Exposed mapping
 * and SQLite dialect all run for real, so the projection has something that can actually fail.
 *
 * Websocket-dependent members (`connect`/`disconnect`/`disconnectAll`, `sendRawMessage`, and the
 * OCPP sends inside `authorize`/`setConnector*`/`setChargePointStatus`/`sendSecurityEvent`) are not
 * covered here — they open real ktor websockets to a CSMS that this harness deliberately does not
 * stand up.
 */
class EmulatorEngineQueryTest : DatabaseSpec({

    val chargePointRepository = ChargePointRepository()
    val chargePointService = ChargePointService(chargePointRepository)
    val previousMessagesService = PreviousMessagesService()

    val engine = DefaultEmulatorEngine(
        connectionManager = ConnectionManager(
            messageInterceptor = MessageInterceptor(chargePointService),
            chargePointRepository = chargePointRepository,
        ),
        chargePointService = chargePointService,
        chargePointConnectorService = ChargePointConnectorService(ChargePointConnectorRepository()),
        chargePointRepository = chargePointRepository,
        chargePointManager = ChargePointManager(),
        previousMessagesService = previousMessagesService,
    )

    fun seedChargePoint(
        identity: String = "MEM_001",
        connectorCount: Int = 1,
    ): ChargePointDAO {
        return chargePointService.upsert(
            name = "Emulator",
            identity = identity,
            password = null,
            ocppUrl = "wss://example.invalid/ocpp",
            apiUrl = "https://example.invalid",
            firmware = "1.2.3",
            maxKw = 22.0,
            connectorCount = connectorCount,
            meterType = MeterType.OCPP,
        )
    }

    describe("getChargePoint") {

        it("projects the charge point and its connectors into a DTO") {
            val seeded = seedChargePoint(connectorCount = 2)

            val chargePoint = engine.getChargePoint(seeded.idValue)

            val connectorPositions = chargePoint.connectors.map { connector -> connector.position }
            val firstConnector = chargePoint.connectors.first()

            chargePoint.id shouldBe seeded.idValue
            chargePoint.identity shouldBe "MEM_001"
            chargePoint.firmware shouldBe "1.2.3"
            chargePoint.maxKw shouldBe 22.0
            chargePoint.meterType shouldBe MeterType.OCPP
            chargePoint.ocppVersion shouldBe OcppVersion.V16
            chargePoint.connectorCount shouldBe 2
            connectorPositions shouldContainExactly listOf(1, 2)
            firstConnector.status shouldBe ChargePointStatus.Available
        }

        it("throws when the charge point cannot be resolved") {
            shouldThrow<ChargePointNotFoundException> {
                engine.getChargePoint(999_999)
            }
        }
    }

    describe("findChargePoint") {

        it("returns the DTO when the charge point exists") {
            val seeded = seedChargePoint()

            val chargePoint = engine.findChargePoint(seeded.idValue)

            chargePoint.shouldNotBeNull()
            chargePoint.identity shouldBe "MEM_001"
        }

        it("returns null instead of throwing when it does not") {
            val chargePoint = engine.findChargePoint(999_999)

            chargePoint.shouldBeNull()
        }
    }

    describe("connector projection") {

        it("maps the active transaction and sums meter readings into meterWh") {
            val chargePoint = seedChargePoint(connectorCount = 1)

            transaction {
                val connector = chargePoint.connectors.first { connector -> connector.position == 1 }
                val activeTransaction = ChargePointTransactionDAO.newInstance(
                    chargePoint = chargePoint,
                    chargePointConnector = connector,
                    externalId = 4242,
                    idTag = "TAG-1",
                )
                activeTransaction.endMeter = 1500.0
                connector.activeTransaction = activeTransaction
            }

            val connectorDto = engine.getChargePoint(chargePoint.idValue).connectors.single()
            val activeTransactionDto = connectorDto.activeTransaction

            connectorDto.hasActiveTransaction shouldBe true
            connectorDto.meterWh shouldBe 1500.0
            activeTransactionDto.shouldNotBeNull()
            activeTransactionDto.externalId shouldBe 4242
            activeTransactionDto.idTag shouldBe "TAG-1"
        }

        it("leaves the active transaction null when the connector is idle") {
            val chargePoint = seedChargePoint(connectorCount = 1)

            val connectorDto = engine.getChargePoint(chargePoint.idValue).connectors.single()
            val activeTransactionDto = connectorDto.activeTransaction

            activeTransactionDto.shouldBeNull()
            connectorDto.hasActiveTransaction shouldBe false
        }
    }

    describe("observeChargePoints") {

        it("emits the current charge points projected to DTOs on subscription") {
            seedChargePoint(identity = "MEM_001", connectorCount = 1)
            seedChargePoint(identity = "MEM_002", connectorCount = 1)

            val chargePoints = engine.observeChargePoints().first()
            val identities = chargePoints.map { chargePoint -> chargePoint.identity }.sorted()

            identities shouldContainExactly listOf("MEM_001", "MEM_002")
        }
    }

    describe("deleteChargePoint") {

        it("removes the charge point together with its connectors and transactions") {
            val chargePoint = seedChargePoint(connectorCount = 2)
            val chargePointId = chargePoint.idValue

            transaction {
                val connector = chargePoint.connectors.first { connector -> connector.position == 1 }
                ChargePointTransactionDAO.newInstance(
                    chargePoint = chargePoint,
                    chargePointConnector = connector,
                    externalId = 1,
                    idTag = "TAG",
                )
            }

            engine.deleteChargePoint(chargePointId)

            transaction {
                val deletedChargePoint = ChargePointDAO.findById(chargePointId)
                val remainingTransactionCount = ChargePointTransactionDAO.all().count()

                deletedChargePoint.shouldBeNull()
                remainingTransactionCount shouldBe 0
            }
        }
    }

    describe("previous messages") {

        it("returns the saved templates newest-first") {
            engine.savePreviousMessage(messageType = "Heartbeat", message = "first")
            engine.savePreviousMessage(messageType = "Heartbeat", message = "second")

            val stored = engine.getPreviousMessages("Heartbeat")
            val storedMessages = stored.map { previousMessage -> previousMessage.message }

            storedMessages shouldContainExactly listOf("second", "first")
        }

        it("deletes only the template with the given id") {
            engine.savePreviousMessage(messageType = "Heartbeat", message = "first")
            engine.savePreviousMessage(messageType = "Heartbeat", message = "second")
            val stored = engine.getPreviousMessages("Heartbeat")
            val secondMessageId = stored.first { previousMessage -> previousMessage.message == "second" }.id

            engine.deletePreviousMessage(secondMessageId)

            val remaining = engine.getPreviousMessages("Heartbeat")
            val remainingMessages = remaining.map { previousMessage -> previousMessage.message }

            remainingMessages shouldContainExactly listOf("first")
        }
    }
})
