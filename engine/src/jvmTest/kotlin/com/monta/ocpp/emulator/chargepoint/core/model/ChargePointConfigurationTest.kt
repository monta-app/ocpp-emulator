package com.monta.ocpp.emulator.chargepoint.core.model

import com.monta.ocpp.emulator.platform.eichrecht.model.EichrechtKey
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class ChargePointConfigurationTest : DescribeSpec({

    describe("defaults") {

        it("ships the OCPP 1.6 core keys a CSMS will ask for on GetConfiguration") {
            val configuration = ChargePointConfiguration()

            configuration.containsKey("HeartbeatInterval") shouldBe true
            configuration.containsKey("MeterValuesSampledData") shouldBe true
            configuration.containsKey("SupportedFeatureProfiles") shouldBe true
            configuration.containsKey("ConnectionTimeOut") shouldBe true
            configuration.containsKey(ChargePointConfiguration.freeChargingKey) shouldBe true
        }

        it("starts with free charging off and a placeholder id tag") {
            val configuration = ChargePointConfiguration()

            configuration.freeCharging shouldBe false
            configuration.freeChargingIdTag shouldBe "FFFFFFFF"
        }

        it("samples meter values every three minutes") {
            ChargePointConfiguration().meterValueSampleInterval shouldBe 180L
        }
    }

    describe("meterValuesSampledData") {

        it("splits the stored comma separated list into measurands") {
            ChargePointConfiguration().meterValuesSampledData shouldContainExactly listOf(
                "Energy.Active.Import.Register",
                "Current.Import",
                "Voltage",
                "Power.Active.Import",
                "SoC",
            )
        }

        it("trims the whitespace a hand-typed CSMS value tends to carry") {
            val configuration = ChargePointConfiguration()
            configuration["MeterValuesSampledData"] = " Voltage , SoC "

            configuration.meterValuesSampledData shouldContainExactly listOf("Voltage", "SoC")
        }

        it("reads as no measurands when the key was cleared") {
            val configuration = ChargePointConfiguration()
            configuration["MeterValuesSampledData"] = null

            configuration.meterValuesSampledData shouldBe emptyList()
        }
    }

    describe("numeric accessors") {

        it("round-trip the heartbeat interval a BootNotification response set") {
            val configuration = ChargePointConfiguration()
            configuration.heartbeatInterval = 900

            configuration["HeartbeatInterval"] shouldBe "900"
            configuration.heartbeatInterval shouldBe 900L
        }

        it("fall back to zero rather than throwing on a value that is not a number") {
            val configuration = ChargePointConfiguration()
            configuration["HeartbeatInterval"] = "not-a-number"
            configuration["MeterValueSampleInterval"] = ""

            configuration.heartbeatInterval shouldBe 0L
            configuration.meterValueSampleInterval shouldBe 0L
        }
    }

    describe("freeCharging") {

        it("round-trips through the string the CSMS sees") {
            val configuration = ChargePointConfiguration()
            configuration.freeCharging = true

            configuration[ChargePointConfiguration.freeChargingKey] shouldBe "true"
            configuration.freeCharging shouldBe true
        }

        it("treats any non-true value as off") {
            val configuration = ChargePointConfiguration()
            configuration[ChargePointConfiguration.freeChargingKey] = "yes"

            configuration.freeCharging shouldBe false
        }
    }

    describe("eichrechtKey") {

        it("mints a key when none has been stored yet") {
            val configuration = ChargePointConfiguration()

            configuration.eichrechtKey.publicKey().isNotEmpty() shouldBe true
        }

        it("returns a different key each time while none is stored") {
            val configuration = ChargePointConfiguration()

            val first = configuration.eichrechtKey.publicKey()
            val second = configuration.eichrechtKey.publicKey()

            (first == second) shouldBe false
        }

        it("returns the stored key once one has been persisted") {
            val configuration = ChargePointConfiguration()
            val key = EichrechtKey.newInstance()

            configuration.eichrechtKey = key

            configuration.eichrechtKey.publicKey() shouldBe key.publicKey()
            configuration.eichrechtKey.privateKey() shouldBe key.privateKey()
        }
    }
})
