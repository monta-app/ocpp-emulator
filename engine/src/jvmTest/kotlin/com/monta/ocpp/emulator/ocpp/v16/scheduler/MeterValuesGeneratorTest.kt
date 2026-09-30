package com.monta.ocpp.emulator.ocpp.v16.scheduler

import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.temporal.ChronoUnit

class MeterValuesGeneratorTest : DescribeSpec({

    val watts = 6900.0

    fun generate(
        vararg measurands: String,
        startTime: Instant? = null,
        endMeter: Double = 0.0,
        numberPhases: Int = 3,
        meterType: MeterType = MeterType.OCPP,
    ) = MeterValuesGenerator.generate(
        meterValuesSampledData = measurands.toList(),
        startTime = startTime,
        endMeter = endMeter,
        watts = watts,
        numberPhases = numberPhases,
        meterType = meterType,
    )

    describe("measurand selection") {

        it("emits nothing when no measurands are configured") {
            generate() shouldBe emptyList()
        }

        it("emits only the measurands that were asked for") {
            val measurands = generate("Voltage").map { sampledValue -> sampledValue.measurand }

            measurands shouldContainExactly listOf("Voltage", "Voltage", "Voltage")
        }

        it("ignores a measurand the generator does not know about") {
            generate("Temperature") shouldBe emptyList()
        }

        it("emits the full set in a stable order") {
            val measurands = generate(
                "Energy.Active.Import.Register",
                "Current.Import",
                "Voltage",
                "Power.Active.Import",
                "SoC",
                startTime = Instant.now(),
            ).map { sampledValue -> sampledValue.measurand }

            measurands shouldContainExactly listOf(
                "Energy.Active.Import.Register",
                "Current.Import",
                "Current.Import",
                "Current.Import",
                "Voltage",
                "Voltage",
                "Voltage",
                "Power.Active.Import",
                "Power.Active.Import",
                "Power.Active.Import",
                "Power.Active.Import",
                "SoC",
            )
        }
    }

    describe("Energy.Active.Import.Register") {

        it("truncates the register to whole watt-hours for a standard meter") {
            val sampledValue = generate(
                "Energy.Active.Import.Register",
                endMeter = 1234.9,
                meterType = MeterType.OCPP,
            ).single()

            sampledValue.value shouldBe "1234"
            sampledValue.unit shouldBe "Wh"
        }

        it("keeps one decimal and adds sub-watt-hour jitter for a high precision meter") {
            val sampledValue = generate(
                "Energy.Active.Import.Register",
                endMeter = 1234.0,
                meterType = MeterType.OcppHighPrecision,
            ).single()

            val reading = sampledValue.value.toDouble()

            reading shouldBeGreaterThanOrEqual 1234.0
            reading shouldBeLessThan 1234.5
        }
    }

    describe("Current.Import") {

        it("splits the load evenly across three phases") {
            val values = generate("Current.Import").map { sampledValue -> sampledValue.value }

            values shouldContainExactly listOf("10.0", "10.0", "10.0")
        }

        it("zeroes the phases the vehicle is not drawing on") {
            val sampledValues = generate("Current.Import", numberPhases = 1)

            sampledValues.map { sampledValue -> sampledValue.value } shouldContainExactly listOf("30.0", "0", "0")
            sampledValues.map { sampledValue -> sampledValue.phase } shouldContainExactly listOf("L1", "L2", "L3")
        }
    }

    describe("Power.Active.Import") {

        it("reports per-phase power plus an unphased total") {
            val sampledValues = generate("Power.Active.Import")

            sampledValues.map { sampledValue -> sampledValue.phase } shouldContainExactly listOf("L1", "L2", "L3", null)
            sampledValues.map { sampledValue -> sampledValue.value } shouldContainExactly
                listOf("2300.0", "2300.0", "2300.0", "6900.0")
        }

        it("keeps the total consistent with the phases the vehicle is drawing on") {
            val sampledValues = generate("Power.Active.Import", numberPhases = 1)

            sampledValues.map { sampledValue -> sampledValue.value } shouldContainExactly
                listOf("6900.0", "0", "0", "6900.0")
        }
    }

    describe("SoC") {

        it("is omitted outside a transaction, because there is no charge to report progress on") {
            generate("SoC", startTime = null) shouldBe emptyList()
        }

        it("starts at 20 percent when the transaction has just begun") {
            val sampledValue = generate("SoC", startTime = Instant.now()).single()

            sampledValue.value shouldBe "20"
            sampledValue.unit shouldBe "Percent"
        }

        it("climbs five percent per elapsed minute") {
            val startTime = Instant.now().minus(10, ChronoUnit.MINUTES)

            generate("SoC", startTime = startTime).single().value shouldBe "70"
        }

        it("clamps at a full battery instead of running past 100 percent") {
            val startTime = Instant.now().minus(4, ChronoUnit.HOURS)

            generate("SoC", startTime = startTime).single().value shouldBe "100"
        }
    }

    describe("sampled value envelope") {

        it("marks every value as a periodic raw sample") {
            val sampledValues = generate(
                "Energy.Active.Import.Register",
                "Current.Import",
                "Voltage",
                "Power.Active.Import",
                "SoC",
                startTime = Instant.now(),
            )

            sampledValues.forEach { sampledValue ->
                sampledValue.context shouldBe "Sample.Periodic"
                sampledValue.format shouldBe "Raw"
            }
        }

        it("reports every phase at nominal mains voltage") {
            val sampledValues = generate("Voltage")

            sampledValues.map { sampledValue -> sampledValue.phase } shouldContainExactly listOf("L1-N", "L2-N", "L3-N")
            sampledValues.map { sampledValue -> sampledValue.value } shouldContainExactly listOf("230", "230", "230")
        }
    }
})
