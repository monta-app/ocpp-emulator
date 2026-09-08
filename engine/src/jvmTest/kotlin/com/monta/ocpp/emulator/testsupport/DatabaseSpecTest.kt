package com.monta.ocpp.emulator.testsupport

import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Guards the contract every Tier 2 spec relies on: each leaf test gets its own empty database.
 *
 * The two tests below are ordered — the first writes a row, the second asserts it is gone. If the
 * `beforeEach` in [DatabaseSpec] ever stopped firing per test, the second would see the first's row
 * and fail, instead of the whole suite quietly sharing state.
 */
class DatabaseSpecTest : DatabaseSpec({

    describe("the throwaway database") {

        it("has the schema applied and starts empty") {
            transaction {
                ChargePointDAO.all().count() shouldBe 0
                ChargePointDAO.newInstance(
                    name = "Emulator",
                    identity = "MEM_ISOLATION",
                    password = null,
                    ocppUrl = "wss://example.invalid/ocpp",
                    apiUrl = "https://example.invalid",
                    firmware = "1.0.0",
                    maxKw = 22.0,
                )
                ChargePointDAO.all().count() shouldBe 1
            }
        }

        it("does not carry rows over from the previous test") {
            transaction {
                ChargePointDAO.all().count() shouldBe 0
            }
        }
    }
})
