package com.monta.ocpp.emulator.chargepoint.core.entity

import com.monta.library.ocpp.v16.core.DataTransferRequest
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationStatus
import com.monta.ocpp.emulator.testsupport.ChargePointFixtures
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ChargePointDAOTest : DatabaseSpec({

    describe("updateConfiguration") {

        it("persists the change rather than mutating the in-memory map") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.updateConfiguration { heartbeatInterval = 600 }
            }

            transaction {
                ChargePointDAO.findById(chargePoint.id)?.configuration?.heartbeatInterval shouldBe 600L
            }
        }

        it("keeps the keys it was not asked to change") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.updateConfiguration { heartbeatInterval = 600 }
            }

            transaction {
                val configuration = ChargePointDAO.findById(chargePoint.id)?.configuration

                configuration?.get("SupportedFeatureProfiles") shouldBe "Core"
                configuration?.meterValueSampleInterval shouldBe 180L
            }
        }

        it("survives repeated updates without losing earlier ones") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.updateConfiguration { heartbeatInterval = 600 }
                chargePoint.updateConfiguration { freeCharging = true }
            }

            transaction {
                val configuration = ChargePointDAO.findById(chargePoint.id)?.configuration

                configuration?.heartbeatInterval shouldBe 600L
                configuration?.freeCharging shouldBe true
            }
        }
    }

    describe("getConnector") {

        it("returns the existing connector for a position that is already provisioned") {
            val chargePoint = ChargePointFixtures.newChargePoint(connectorCount = 2)

            val first = transaction { chargePoint.getConnector(1).id }
            val again = transaction { chargePoint.getConnector(1).id }

            again shouldBe first
        }

        it("provisions a connector on demand for a position that does not exist yet") {
            val chargePoint = ChargePointFixtures.newChargePoint(connectorCount = 1)

            transaction { chargePoint.getConnector(4) }

            transaction {
                chargePoint.connectors.map { connector -> connector.position }.sorted() shouldContainExactly
                    listOf(1, 4)
            }
        }

        it("gives a provisioned connector the charge point's rating and identity") {
            val chargePoint = ChargePointFixtures.newChargePoint(connectorCount = 1, maxKw = 11.0)

            transaction {
                val connector = chargePoint.getConnector(2)

                connector.maxKw shouldBe 11.0
                connector.chargePointIdentity shouldBe "MEM_001"
            }
        }
    }

    describe("canPerformAction") {

        it("is allowed while the firmware is idle") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction { chargePoint.canPerformAction } shouldBe true
        }

        it("is blocked mid firmware download") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction { chargePoint.firmwareStatus = FirmwareStatusNotificationStatus.Downloading }

            transaction { chargePoint.canPerformAction } shouldBe false
        }

        it("is blocked mid firmware install") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction { chargePoint.firmwareStatus = FirmwareStatusNotificationStatus.Installing }

            transaction { chargePoint.canPerformAction } shouldBe false
        }

        it("is allowed again once the firmware is installed") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction { chargePoint.firmwareStatus = FirmwareStatusNotificationStatus.Installed }

            transaction { chargePoint.canPerformAction } shouldBe true
        }
    }

    describe("handleDataTransferRequest") {

        fun dataTransfer(
            messageId: String,
            data: String?,
            vendorId: String = "com.monta",
        ): DataTransferRequest {
            return DataTransferRequest(
                vendorId = vendorId,
                messageId = messageId,
                data = data,
            )
        }

        it("ignores a vendor it does not speak for") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.handleDataTransferRequest(
                    dataTransfer(messageId = "SoC", data = "50", vendorId = "com.example"),
                ) shouldBe false
            }
        }

        it("ignores a Monta message it does not recognise") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.handleDataTransferRequest(dataTransfer(messageId = "Unknown", data = "x")) shouldBe false
                chargePoint.displayText shouldBe "\n\n\n\n"
            }
        }

        it("writes the smart charging banner on the first display line") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.handleDataTransferRequest(
                    dataTransfer(messageId = "SmartChargingEnabled", data = "true"),
                ) shouldBe true

                chargePoint.displayText.split("\n")[0] shouldBe "Smart Charging"
            }
        }

        it("falls back to the plain charging banner when smart charging is off") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.handleDataTransferRequest(dataTransfer(messageId = "SmartChargingEnabled", data = "false"))

                chargePoint.displayText.split("\n")[0] shouldBe "Charging"
            }
        }

        it("writes each schedule field on its own line") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.handleDataTransferRequest(dataTransfer(messageId = "StartTime", data = "13:00"))
                chargePoint.handleDataTransferRequest(dataTransfer(messageId = "EndTime", data = "17:00"))
                chargePoint.handleDataTransferRequest(dataTransfer(messageId = "SoC", data = "80"))

                chargePoint.displayText.split("\n") shouldContainExactly listOf(
                    "",
                    "",
                    "Start at: 13:00",
                    "Will be finished at: 17:00",
                    "Battery at: 80%",
                )
            }
        }

        it("blanks every line on ClearDisplay") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.handleDataTransferRequest(dataTransfer(messageId = "SoC", data = "80"))
                chargePoint.handleDataTransferRequest(dataTransfer(messageId = "ClearDisplay", data = null)) shouldBe
                    true

                chargePoint.displayText shouldBe "\n\n\n\n"
            }
        }

        it("persists the display text so a reconnect does not blank the screen") {
            val chargePoint = ChargePointFixtures.newChargePoint()

            transaction {
                chargePoint.handleDataTransferRequest(dataTransfer(messageId = "SoC", data = "80"))
            }

            transaction {
                ChargePointDAO.findById(chargePoint.id)?.displayText?.split("\n")?.get(4) shouldBe "Battery at: 80%"
            }
        }
    }

    describe("normalizeIdentity") {

        it("upper-cases and trims, because that is the stored form every query has to match") {
            ChargePointDAO.normalizeIdentity("  mem_001  ") shouldBe "MEM_001"
            ChargePointDAO.normalizeIdentity("MEM_001") shouldBe "MEM_001"
        }
    }
})
