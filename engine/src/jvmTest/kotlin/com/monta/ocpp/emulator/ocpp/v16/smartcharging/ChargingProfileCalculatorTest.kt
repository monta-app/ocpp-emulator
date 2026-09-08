package com.monta.ocpp.emulator.ocpp.v16.smartcharging

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedule
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedulePeriod
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.ZoneOffset

/**
 * One row of the schedule-resolution matrix: where we are in the schedule, and the wattage the
 * charge point should draw at that moment.
 */
private data class ResolutionCase(
    val description: String,
    val secondsIntoSchedule: Long,
    val expectedWatts: Double?,
)

/** One row of the phase-scaling matrix. */
private data class PhaseCase(
    val phases: Int,
    val expectedWatts: Double,
)

private const val VOLTAGE = 230.0

private fun watts(
    amps: Double,
    phases: Int,
): Double {
    return amps * VOLTAGE * phases
}

/**
 * Tier 1 — pure logic, no database, no clock.
 *
 * Every case pins one branch of the OCPP 1.6 §7.8 charging-schedule resolution against a fixed
 * `now`, so a failure names the rule that broke rather than "smart charging is off".
 */
class ChargingProfileCalculatorTest : DescribeSpec({

    val scheduleStart: Instant = Instant.parse("2026-01-01T12:00:00Z")

    fun period(
        startPeriod: Int,
        limit: Double,
        numberPhases: Int = 3,
    ): ChargingSchedulePeriod {
        return ChargingSchedulePeriod(
            startPeriod = startPeriod,
            limit = limit,
            numberPhases = numberPhases,
        )
    }

    fun profileOf(
        periods: List<ChargingSchedulePeriod>,
        startSchedule: Instant? = scheduleStart,
        durationSeconds: Int? = null,
        minChargingRate: Double? = null,
    ): ChargingProfile {
        return ChargingProfile(
            chargingProfileId = 1,
            chargingSchedule = ChargingSchedule(
                duration = durationSeconds,
                startSchedule = startSchedule?.atZone(ZoneOffset.UTC),
                chargingSchedulePeriod = periods,
                minChargingRate = minChargingRate,
            ),
        )
    }

    fun wattsAt(
        profile: ChargingProfile?,
        secondsIntoSchedule: Long,
    ): Double? {
        return ChargingProfileCalculator.getWatts(
            chargingProfile = profile,
            transactionStartedAt = scheduleStart,
            now = scheduleStart.plusSeconds(secondsIntoSchedule),
        )
    }

    describe("getWatts") {

        describe("when there is nothing to resolve") {

            it("returns null without a charging profile") {
                val actualWatts = wattsAt(profile = null, secondsIntoSchedule = 0)

                actualWatts shouldBe null
            }

            it("returns null when the profile carries no schedule") {
                val profile = ChargingProfile(chargingProfileId = 1, chargingSchedule = null)

                val actualWatts = wattsAt(profile = profile, secondsIntoSchedule = 0)

                actualWatts shouldBe null
            }

            it("returns null before the schedule starts") {
                val profile = profileOf(periods = listOf(period(startPeriod = 0, limit = 16.0)))

                val actualWatts = wattsAt(profile = profile, secondsIntoSchedule = -1)

                actualWatts shouldBe null
            }

            it("returns null while the first period is still in the future") {
                val profile = profileOf(periods = listOf(period(startPeriod = 300, limit = 16.0)))

                val actualWatts = wattsAt(profile = profile, secondsIntoSchedule = 60)

                actualWatts shouldBe null
            }
        }

        describe("resolving the period in force") {

            // v1.6 §7.8: the applicable period is the last one whose startPeriod has elapsed.
            // Declared out of order on purpose — the resolution has to sort them.
            val steppedSchedule = profileOf(
                periods = listOf(
                    period(startPeriod = 3600, limit = 32.0),
                    period(startPeriod = 0, limit = 16.0),
                ),
            )

            withData(
                nameFn = { resolutionCase -> resolutionCase.description },
                ResolutionCase("draws 16 A at the start of the schedule", 0, watts(16.0, 3)),
                ResolutionCase("still draws 16 A ten minutes in", 600, watts(16.0, 3)),
                ResolutionCase("still draws 16 A a second before the step", 3599, watts(16.0, 3)),
                ResolutionCase("steps up to 32 A the moment the period elapses", 3600, watts(32.0, 3)),
                ResolutionCase("holds 32 A once the last period is in force", 7200, watts(32.0, 3)),
            ) { resolutionCase ->
                val actualWatts = wattsAt(
                    profile = steppedSchedule,
                    secondsIntoSchedule = resolutionCase.secondsIntoSchedule,
                )

                actualWatts shouldBe resolutionCase.expectedWatts
            }
        }

        describe("schedule bounds") {

            val boundedSchedule = profileOf(
                periods = listOf(period(startPeriod = 0, limit = 16.0)),
                durationSeconds = 3600,
            )

            it("still applies part way through a schedule that declares a duration") {
                val actualWatts = wattsAt(profile = boundedSchedule, secondsIntoSchedule = 600)

                actualWatts shouldBe watts(16.0, 3)
            }

            it("stops applying once the duration has elapsed") {
                val actualWatts = wattsAt(profile = boundedSchedule, secondsIntoSchedule = 3601)

                actualWatts shouldBe null
            }

            it("holds the last period open when no duration is declared") {
                val unboundedSchedule = profileOf(periods = listOf(period(startPeriod = 0, limit = 16.0)))

                val actualWatts = wattsAt(profile = unboundedSchedule, secondsIntoSchedule = 60 * 60 * 24)

                actualWatts shouldBe watts(16.0, 3)
            }

            it("anchors a relative schedule to the transaction start") {
                val relativeSchedule = profileOf(
                    periods = listOf(period(startPeriod = 0, limit = 16.0)),
                    startSchedule = null,
                )

                val actualWatts = wattsAt(profile = relativeSchedule, secondsIntoSchedule = 0)

                actualWatts shouldBe watts(16.0, 3)
            }
        }

        describe("converting the limit to watts") {

            withData(
                nameFn = { phaseCase -> "scales a 16 A limit across ${phaseCase.phases} phase(s)" },
                PhaseCase(phases = 1, expectedWatts = watts(16.0, 1)),
                PhaseCase(phases = 2, expectedWatts = watts(16.0, 2)),
                PhaseCase(phases = 3, expectedWatts = watts(16.0, 3)),
            ) { phaseCase ->
                val profile = profileOf(
                    periods = listOf(
                        period(startPeriod = 0, limit = 16.0, numberPhases = phaseCase.phases),
                    ),
                )

                val actualWatts = wattsAt(profile = profile, secondsIntoSchedule = 0)

                actualWatts shouldBe phaseCase.expectedWatts
            }

            it("never returns less than the minimum charging rate") {
                val profile = profileOf(
                    periods = listOf(period(startPeriod = 0, limit = 6.0)),
                    minChargingRate = 10.0,
                )

                val actualWatts = wattsAt(profile = profile, secondsIntoSchedule = 0)

                actualWatts shouldBe watts(10.0, 3)
            }

            it("leaves a limit above the minimum charging rate untouched") {
                val profile = profileOf(
                    periods = listOf(period(startPeriod = 0, limit = 16.0)),
                    minChargingRate = 10.0,
                )

                val actualWatts = wattsAt(profile = profile, secondsIntoSchedule = 0)

                actualWatts shouldBe watts(16.0, 3)
            }
        }
    }
})
