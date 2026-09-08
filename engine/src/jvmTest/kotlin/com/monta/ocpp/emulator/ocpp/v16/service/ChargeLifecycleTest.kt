package com.monta.ocpp.emulator.ocpp.v16.service

import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.testsupport.EmulatorHarness
import com.monta.ocpp.emulator.testsupport.EmulatorSpec
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Duration.Companion.seconds

class ChargeLifecycleTest : EmulatorSpec({

    describe("starting a charge") {

        it("tells the CSMS and records the transaction it hands back") {
            val emulator = EmulatorHarness.start()

            emulator.engine.authorize(emulator.connectorId, "ABCD1234")

            emulator.csms.actions() shouldContain "Authorize"
            val startTransaction = emulator.csms.lastOf("StartTransaction").shouldNotBeNull()
            startTransaction.payload.get("idTag").asString() shouldBe "ABCD1234"
            startTransaction.payload.get("connectorId").asInt() shouldBe 1

            transaction {
                val stored = ChargePointTransactionDAO.all().single()

                stored.idTag shouldBe "ABCD1234"
                stored.externalId shouldBe 1000
                stored.endTime.shouldBeNull()
            }
        }

        it("points the connector at the new transaction") {
            val emulator = EmulatorHarness.start()

            emulator.engine.authorize(emulator.connectorId, "ABCD1234")

            transaction {
                val connector = emulator.chargePoint().getConnector(1)

                connector.activeTransaction.shouldNotBeNull()
                connector.activeTransaction?.externalId shouldBe 1000
            }
        }

        it("publishes the resulting connector status to the CSMS") {
            val emulator = EmulatorHarness.start()

            emulator.engine.authorize(emulator.connectorId, "ABCD1234")

            val statuses = emulator.csms.allOf("StatusNotification").map { frame ->
                frame.payload.get("status").asString()
            }

            statuses shouldContain "Preparing"
            statuses shouldContain "Charging"
        }

        it("does not start a charge the CSMS refused to authorize") {
            val emulator = EmulatorHarness.start()
            emulator.csms.respondTo("Authorize", """{"idTagInfo":{"status":"Blocked"}}""")

            emulator.engine.authorize(emulator.connectorId, "ABCD1234")

            emulator.csms.countOf("StartTransaction") shouldBe 0
            transaction { ChargePointTransactionDAO.all().count() } shouldBe 0
        }

        it("does not record a transaction the CSMS rejected at StartTransaction") {
            val emulator = EmulatorHarness.start()
            emulator.csms.respondTo(
                "StartTransaction",
                """{"transactionId":0,"idTagInfo":{"status":"Invalid"}}""",
            )

            emulator.engine.authorize(emulator.connectorId, "ABCD1234")

            emulator.csms.countOf("StartTransaction") shouldBe 1
            transaction { ChargePointTransactionDAO.all().count() } shouldBe 0
        }

        it("plugs the car in when a charge starts on an unplugged connector") {
            val emulator = EmulatorHarness.start()
            transaction { emulator.chargePoint().getConnector(1).carState = CarState.A }

            emulator.engine.authorize(emulator.connectorId, "ABCD1234")

            transaction { emulator.chargePoint().getConnector(1).carState } shouldBe CarState.B
        }
    }

    describe("stopping a charge") {

        it("tells the CSMS which transaction ended and why") {
            val emulator = EmulatorHarness.start()
            emulator.engine.authorize(emulator.connectorId, "ABCD1234")
            emulator.csms.clear()

            emulator.engine.stopTransaction(
                connectorId = emulator.connectorId,
                reason = Reason.Remote,
                endReasonDescription = "stopped from the app",
            )

            val stopTransaction = emulator.csms.lastOf("StopTransaction").shouldNotBeNull()
            stopTransaction.payload.get("transactionId").asInt() shouldBe 1000
            stopTransaction.payload.get("idTag").asString() shouldBe "ABCD1234"
            stopTransaction.payload.get("reason").asString() shouldBe "Remote"
        }

        it("closes the transaction out and clears it off the connector") {
            val emulator = EmulatorHarness.start()
            emulator.engine.authorize(emulator.connectorId, "ABCD1234")

            emulator.engine.stopTransaction(
                connectorId = emulator.connectorId,
                reason = Reason.Remote,
                endReasonDescription = "stopped from the app",
            )

            transaction {
                val stored = ChargePointTransactionDAO.all().single()

                stored.endTime.shouldNotBeNull()
                stored.endReason shouldBe Reason.Remote
                stored.endReasonDescription shouldBe "stopped from the app"
                stored.canStop() shouldBe false
                emulator.chargePoint().getConnector(1).activeTransaction.shouldBeNull()
                emulator.chargePoint().getActiveTransactions() shouldBe emptyList()
            }
        }

        it("reports the connector as Finishing once the charge is over") {
            val emulator = EmulatorHarness.start()
            emulator.engine.authorize(emulator.connectorId, "ABCD1234")
            emulator.csms.clear()

            emulator.engine.stopTransaction(connectorId = emulator.connectorId, reason = Reason.Local)

            val statuses = emulator.csms.allOf("StatusNotification").map { frame ->
                frame.payload.get("status").asString()
            }

            statuses shouldContain "Finishing"
        }

        it("sends nothing when there is no charge running") {
            val emulator = EmulatorHarness.start()
            emulator.csms.clear()

            emulator.engine.stopTransaction(connectorId = emulator.connectorId, reason = Reason.Local)

            emulator.csms.countOf("StopTransaction") shouldBe 0
        }

        it("stops the charge when the car is unplugged") {
            val emulator = EmulatorHarness.start()
            emulator.engine.authorize(emulator.connectorId, "ABCD1234")
            emulator.csms.clear()

            emulator.engine.setConnectorCarState(emulator.connectorId, CarState.A)

            emulator.csms.countOf("StopTransaction") shouldBe 1
            transaction { ChargePointTransactionDAO.all().single().endTime }.shouldNotBeNull()
        }
    }

    describe("meter values") {

        it("addresses the running transaction on the right connector") {
            val emulator = EmulatorHarness.start(connectorCount = 2)
            emulator.engine.authorize(emulator.connectorIds[1], "ABCD1234")
            emulator.csms.clear()

            emulator.chargePointManager.sendMeterValues(emulator.chargePoint(), connectorId = 2)

            val meterValues = emulator.csms.lastOf("MeterValues").shouldNotBeNull()
            meterValues.payload.get("connectorId").asInt() shouldBe 2
            meterValues.payload.get("transactionId").asInt() shouldBe 1000
        }

        it("carries the measurands the charge point is configured to sample") {
            val emulator = EmulatorHarness.start()
            emulator.engine.authorize(emulator.connectorId, "ABCD1234")
            emulator.csms.clear()

            emulator.chargePointManager.sendMeterValues(emulator.chargePoint(), connectorId = 1)

            val meterValues = emulator.csms.lastOf("MeterValues").shouldNotBeNull()
            val sampledValues = meterValues.payload.get("meterValue").get(0).get("sampledValue")
            val measurands = (0 until sampledValues.size()).map { index ->
                sampledValues.get(index).get("measurand").asString()
            }

            measurands shouldContain "Energy.Active.Import.Register"
            measurands shouldContain "Current.Import"
            measurands shouldContain "Voltage"
            measurands shouldContain "Power.Active.Import"
            measurands shouldContain "SoC"
        }

        it("omits the transaction id when nothing is charging") {
            val emulator = EmulatorHarness.start()
            emulator.csms.clear()

            emulator.chargePointManager.sendMeterValues(emulator.chargePoint(), connectorId = 1)

            val meterValues = emulator.csms.lastOf("MeterValues").shouldNotBeNull()
            meterValues.payload.has("transactionId") shouldBe false
        }

        it("covers every connector when the CSMS does not name one") {
            val emulator = EmulatorHarness.start(connectorCount = 2)
            emulator.csms.clear()

            emulator.chargePointManager.sendMeterValues(emulator.chargePoint(), connectorId = null)

            val connectorIds = emulator.csms.allOf("MeterValues").map { frame ->
                frame.payload.get("connectorId").asInt()
            }

            connectorIds shouldBe listOf(1, 2)
        }
    }

    describe("boot sequence") {

        it("announces itself and comes up Available") {
            val emulator = EmulatorHarness.start(booted = false)

            emulator.chargePointManager.startBootSequence(emulator.chargePoint())

            val bootNotification = emulator.csms.lastOf("BootNotification").shouldNotBeNull()
            bootNotification.payload.get("chargePointVendor").asString() shouldBe "Monta"
            bootNotification.payload.get("firmwareVersion").asString() shouldBe "1.0.0"

            val statuses = emulator.csms.allOf("StatusNotification").map { frame ->
                frame.payload.get("status").asString()
            }
            statuses shouldContain "Available"

            transaction { emulator.chargePoint().bootedAt }.shouldNotBeNull()
        }

        it("adopts the heartbeat interval the CSMS asked for") {
            val emulator = EmulatorHarness.start(booted = false)
            emulator.csms.respondTo(
                "BootNotification",
                """{"status":"Accepted","currentTime":"2026-01-01T12:00:00Z","interval":42}""",
            )

            emulator.chargePointManager.startBootSequence(emulator.chargePoint())

            transaction { emulator.chargePoint().configuration.heartbeatInterval } shouldBe 42L
        }

        it("publishes its meter public key so the CSMS can verify signed readings") {
            val emulator = EmulatorHarness.start(booted = false, meterType = MeterType.Eichrecht)

            emulator.chargePointManager.startBootSequence(emulator.chargePoint())

            val dataTransfer = emulator.csms.lastOf("DataTransfer").shouldNotBeNull()
            dataTransfer.payload.get("vendorId").asString() shouldBe "generalConfiguration"
            dataTransfer.payload.get("messageId").asString() shouldBe "setMeterConfiguration"
            dataTransfer.payload.get("data").asString().contains("publicKey") shouldBe true
        }

        it("does not come up when the CSMS rejects the boot") {
            val emulator = EmulatorHarness.start(booted = false)
            emulator.csms.respondTo(
                "BootNotification",
                """{"status":"Rejected","currentTime":"2026-01-01T12:00:00Z","interval":1}""",
            )

            emulator.chargePointManager.startBootSequence(emulator.chargePoint())

            emulator.csms.actions() shouldNotContain "DataTransfer"
            transaction { emulator.chargePoint().bootedAt }.shouldBeNull()
        }
    }

    describe("CSMS-initiated commands") {

        it("starts a charge on RemoteStartTransaction") {
            val emulator = EmulatorHarness.start()

            emulator.csms.send(
                action = "RemoteStartTransaction",
                payloadJson = """{"connectorId":1,"idTag":"REMOTE01"}""",
            )

            eventually(10.seconds) {
                emulator.csms.countOf("StartTransaction") shouldBe 1
                transaction { ChargePointTransactionDAO.all().single().idTag } shouldBe "REMOTE01"
            }
        }

        it("stops the named charge on RemoteStopTransaction") {
            val emulator = EmulatorHarness.start()
            emulator.engine.authorize(emulator.connectorId, "ABCD1234")
            emulator.csms.clear()

            emulator.csms.send(
                action = "RemoteStopTransaction",
                payloadJson = """{"transactionId":1000}""",
            )

            eventually(10.seconds) {
                emulator.csms.countOf("StopTransaction") shouldBe 1
                transaction { ChargePointTransactionDAO.all().single().endTime }.shouldNotBeNull()
            }
        }

        it("applies a ChangeConfiguration for a key the charge point knows") {
            val emulator = EmulatorHarness.start()

            emulator.csms.send(
                action = "ChangeConfiguration",
                payloadJson = """{"key":"MeterValueSampleInterval","value":"60"}""",
            )

            eventually(10.seconds) {
                transaction { emulator.chargePoint().configuration.meterValueSampleInterval } shouldBe 60L
            }
        }

        it("reports the connector status on TriggerMessage") {
            val emulator = EmulatorHarness.start()
            emulator.csms.clear()

            emulator.csms.send(
                action = "TriggerMessage",
                payloadJson = """{"requestedMessage":"StatusNotification","connectorId":1}""",
            )

            eventually(10.seconds) {
                emulator.csms.countOf("StatusNotification") shouldBe 1
            }
        }
    }

    describe("heartbeat") {

        it("goes out on demand") {
            val emulator = EmulatorHarness.start()
            emulator.csms.clear()

            emulator.chargePointManager.heartbeat(emulator.chargePoint())

            emulator.csms.countOf("Heartbeat") shouldBe 1
        }
    }

    describe("connector status") {

        it("publishes an explicitly requested status") {
            val emulator = EmulatorHarness.start()
            emulator.csms.clear()

            emulator.engine.setConnectorStatus(emulator.connectorId, ChargePointStatus.Unavailable)

            val statusNotification = emulator.csms.lastOf("StatusNotification").shouldNotBeNull()
            statusNotification.payload.get("status").asString() shouldBe "Unavailable"
            statusNotification.payload.get("connectorId").asInt() shouldBe 1
        }

        it("does not re-publish a status the connector is already in") {
            val emulator = EmulatorHarness.start()
            emulator.engine.setConnectorStatus(emulator.connectorId, ChargePointStatus.Unavailable)
            emulator.csms.clear()

            emulator.engine.setConnectorStatus(emulator.connectorId, ChargePointStatus.Unavailable)

            emulator.csms.countOf("StatusNotification") shouldBe 0
        }
    }
})
