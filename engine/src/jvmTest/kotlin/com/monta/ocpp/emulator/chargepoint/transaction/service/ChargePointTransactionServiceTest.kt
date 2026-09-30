package com.monta.ocpp.emulator.chargepoint.transaction.service

import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.chargepoint.transaction.repository.ChargePointTransactionRepository
import com.monta.ocpp.emulator.testsupport.ChargePointFixtures
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant

class ChargePointTransactionServiceTest : DatabaseSpec({

    val service = ChargePointTransactionService(ChargePointTransactionRepository())

    describe("create") {

        it("stores a transaction against the connector that started it") {
            val chargePoint = ChargePointFixtures.newChargePoint(connectorCount = 2)
            val connector = ChargePointFixtures.connectorOf(chargePoint, position = 2)

            val chargePointTransaction = service.create(
                chargePoint = chargePoint,
                chargePointConnector = connector,
                externalId = 4321,
                idTag = "ABCD",
            )

            transaction {
                chargePointTransaction.externalId shouldBe 4321
                chargePointTransaction.idTag shouldBe "ABCD"
                chargePointTransaction.connectorPosition shouldBe 2
                chargePointTransaction.isOwner(chargePoint) shouldBe true
                chargePointTransaction.isOwner(connector) shouldBe true
            }
        }

        it("opens the transaction, so it is stoppable and counted as active") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)

            val chargePointTransaction = service.create(
                chargePoint = chargePoint,
                chargePointConnector = connector,
                externalId = 1,
                idTag = "ABCD",
            )

            transaction {
                chargePointTransaction.endTime.shouldBeNull()
                chargePointTransaction.canStop() shouldBe true
                chargePoint.getActiveTransactions().map { active -> active.externalId } shouldBe listOf(1)
            }
        }

        it("starts the meter where it started, so an unmetered charge bills nothing") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)

            val chargePointTransaction = service.create(
                chargePoint = chargePoint,
                chargePointConnector = connector,
                externalId = 1,
                idTag = "ABCD",
            )

            transaction {
                chargePointTransaction.startMeter shouldBe 0.0
                chargePointTransaction.endMeter shouldBe chargePointTransaction.startMeter
            }
        }
    }

    describe("getByExternalId") {

        it("finds the transaction the CSMS knows about") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            service.create(chargePoint, connector, externalId = 4321, idTag = "ABCD")

            transaction { service.getByExternalId(4321)?.idTag } shouldBe "ABCD"
        }

        it("returns null for an id the CSMS never issued") {
            ChargePointFixtures.newChargePoint()

            service.getByExternalId(9999).shouldBeNull()
        }
    }

    describe("update") {

        it("applies the change and persists it") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            service.create(chargePoint, connector, externalId = 4321, idTag = "ABCD")
            val endTime = Instant.parse("2026-01-01T12:00:00Z")

            service.update(4321) {
                this.endTime = endTime
                this.endReason = Reason.Remote
                this.endMeter = 7900.0
            }.shouldNotBeNull()

            transaction {
                val stored = ChargePointTransactionDAO.all().single()

                stored.endTime shouldBe endTime
                stored.endReason shouldBe Reason.Remote
                stored.endMeter shouldBe 7900.0
                stored.canStop() shouldBe false
            }
        }

        it("is a no-op returning null when the CSMS names a transaction we never started") {
            ChargePointFixtures.newChargePoint()

            service.update(9999) { this.endMeter = 1.0 }.shouldBeNull()
        }

        it("closes the transaction out of the active set") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            service.create(chargePoint, connector, externalId = 4321, idTag = "ABCD")

            service.update(4321) { this.endTime = Instant.now() }

            transaction { chargePoint.getActiveTransactions() } shouldBe emptyList()
        }
    }
})
