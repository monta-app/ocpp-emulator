package com.monta.ocpp.emulator.platform.config.service

import com.monta.ocpp.emulator.platform.config.entity.AppConfigDAO
import com.monta.ocpp.emulator.platform.config.repository.AppConfigRepository
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class AppConfigServiceTest : DatabaseSpec({

    val service = AppConfigService(AppConfigRepository())

    describe("getOrCreate") {

        it("seeds the default the first time a key is read") {
            transaction { service.getOrCreate("ocppUrl", "wss://ocpp.monta.app").value } shouldBe
                "wss://ocpp.monta.app"
        }

        it("does not overwrite a value the user already set") {
            service.upsert("ocppUrl", "wss://ocpp.dev.monta.app")

            transaction { service.getOrCreate("ocppUrl", "wss://ocpp.monta.app").value } shouldBe
                "wss://ocpp.dev.monta.app"
        }

        it("seeds the row only once, so the unique key is never violated") {
            service.getOrCreate("ocppUrl", "wss://ocpp.monta.app")
            service.getOrCreate("ocppUrl", "wss://ocpp.monta.app")

            transaction { AppConfigDAO.all().count() } shouldBe 1
        }
    }

    describe("getByKey") {

        it("returns null for a key that was never written") {
            service.getByKey("missing").shouldBeNull()
        }

        it("returns the stored value") {
            service.upsert("apiUrl", "https://app.monta.app")

            service.getByKey("apiUrl") shouldBe "https://app.monta.app"
        }

        it("distinguishes a stored null from a missing key only by the row existing") {
            service.upsert("apiUrl", null)

            service.getByKey("apiUrl").shouldBeNull()
            transaction { AppConfigDAO.all().count() } shouldBe 1
        }
    }

    describe("upsert") {

        it("creates the row when the key is new") {
            service.upsert("apiUrl", "https://app.monta.app")

            transaction { AppConfigDAO.all().single().key } shouldBe "apiUrl"
        }

        it("updates in place rather than adding a second row") {
            service.upsert("apiUrl", "https://app.dev.monta.app")
            service.upsert("apiUrl", "https://app.monta.app")

            transaction { AppConfigDAO.all().count() } shouldBe 1
            service.getByKey("apiUrl") shouldBe "https://app.monta.app"
        }

        it("writes a whole settings screen in one transaction") {
            val written = service.upsert(
                "ocppUrl" to "wss://ocpp.monta.app",
                "apiUrl" to "https://app.monta.app",
                "vehicleServiceUrl" to null,
            )

            written shouldHaveSize 3
            service.getByKey("ocppUrl") shouldBe "wss://ocpp.monta.app"
            service.getByKey("apiUrl") shouldBe "https://app.monta.app"
            service.getByKey("vehicleServiceUrl").shouldBeNull()
        }
    }
})
