package com.monta.ocpp.emulator.platform.config.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class UrlChoiceTest : DescribeSpec({

    describe("fromUrl") {

        it("recognises each built-in environment by its OCPP URL") {
            UrlChoice.fromUrl("ws://localhost:8000") shouldBe UrlChoice.Local
            UrlChoice.fromUrl("wss://ocpp.dev.monta.app") shouldBe UrlChoice.Dev
            UrlChoice.fromUrl("wss://ocpp.staging.monta.app") shouldBe UrlChoice.Staging
            UrlChoice.fromUrl("wss://ocpp.monta.app") shouldBe UrlChoice.Production
        }

        it("falls back to Other for a custom URL") {
            UrlChoice.fromUrl("wss://ocpp.example.invalid") shouldBe UrlChoice.Other
        }

        it("falls back to Other when nothing has been configured") {
            UrlChoice.fromUrl(null) shouldBe UrlChoice.Other
        }
    }

    describe("fromVehicleServiceUrl") {

        it("recognises each environment that has a vehicle service") {
            UrlChoice.fromVehicleServiceUrl("https://vehicles.dev.monta.app") shouldBe UrlChoice.Dev
            UrlChoice.fromVehicleServiceUrl("https://vehicles.staging.monta.app") shouldBe UrlChoice.Staging
            UrlChoice.fromVehicleServiceUrl("https://vehicles.monta.app") shouldBe UrlChoice.Production
        }

        it("falls back to Other for a custom URL") {
            UrlChoice.fromVehicleServiceUrl("https://vehicles.example.invalid") shouldBe UrlChoice.Other
        }

        it("resolves an empty URL to Local, the first environment declaring one") {
            UrlChoice.fromVehicleServiceUrl("") shouldBe UrlChoice.Local
            UrlChoice.fromUrl("") shouldBe UrlChoice.Other
        }

        it("falls back to Other when nothing has been configured") {
            UrlChoice.fromVehicleServiceUrl(null) shouldBe UrlChoice.Other
        }
    }
})
