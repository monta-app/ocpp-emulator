package com.monta.ocpp.emulator.chargepoint.core.service

import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class PreviousMessagesServiceTest : DatabaseSpec({

    val service = PreviousMessagesService()

    describe("insertNewMessage") {

        it("offers the most recently used message first") {
            service.insertNewMessage("Heartbeat", "first")
            service.insertNewMessage("Heartbeat", "second")
            service.insertNewMessage("Heartbeat", "third")

            transaction {
                service.getAllOfMessageType("Heartbeat").map { stored -> stored.message } shouldContainExactly
                    listOf("third", "second", "first")
            }
        }

        it("moves a repeated message to the top instead of storing it twice") {
            service.insertNewMessage("Heartbeat", "first")
            service.insertNewMessage("Heartbeat", "second")
            service.insertNewMessage("Heartbeat", "first")

            transaction {
                service.getAllOfMessageType("Heartbeat").map { stored -> stored.message } shouldContainExactly
                    listOf("first", "second")
            }
        }

        it("trims the message it stores, so trailing whitespace does not defeat the de-duplication") {
            service.insertNewMessage("Heartbeat", "  padded  ")

            transaction {
                service.getAllOfMessageType("Heartbeat").single().message shouldBe "padded"
            }
        }

        it("keeps histories for different message types apart") {
            service.insertNewMessage("Heartbeat", "shared")
            service.insertNewMessage("StatusNotification", "shared")

            transaction {
                service.getAllOfMessageType("Heartbeat").map { stored -> stored.message } shouldBe listOf("shared")
                service.getAllOfMessageType("StatusNotification").map { stored -> stored.message } shouldBe
                    listOf("shared")
            }
        }
    }

    describe("getAllOfMessageType") {

        it("is empty for a type nothing was ever sent for") {
            service.getAllOfMessageType("Heartbeat") shouldBe emptyList()
        }
    }

    describe("deleteMessage") {

        it("removes only the entry that was asked for") {
            service.insertNewMessage("Heartbeat", "keep")
            service.insertNewMessage("Heartbeat", "drop")
            val toDelete = transaction {
                service.getAllOfMessageType("Heartbeat").first { stored -> stored.message == "drop" }.idValue
            }

            service.deleteMessage(toDelete)

            transaction {
                service.getAllOfMessageType("Heartbeat").map { stored -> stored.message } shouldContainExactly
                    listOf("keep")
            }
        }

        it("is a no-op for an id that is not in the history") {
            service.insertNewMessage("Heartbeat", "keep")

            service.deleteMessage(9999)

            transaction {
                service.getAllOfMessageType("Heartbeat").map { stored -> stored.message } shouldContainExactly
                    listOf("keep")
            }
        }
    }
})
