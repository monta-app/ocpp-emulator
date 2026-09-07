package com.monta.ocpp.emulator.ocpp.v16.smartcharging

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedule
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedulePeriod
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tier 1 — pure logic, no database, no clock.
 *
 * Every case pins one branch of the OCPP 1.6 §7.8 charging-schedule resolution against a fixed
 * `now`, so a failure names the rule that broke rather than "smart charging is off".
 */
class ChargingProfileCalculatorTest {

    private val scheduleStart: Instant = Instant.parse("2026-01-01T12:00:00Z")

    // 16 A over 3 phases at 230 V
    private val watts16AThreePhase = 16.0 * 230.0 * 3

    @Test
    fun `returns null when there is no charging profile`() {
        assertNull(
            ChargingProfileCalculator.getWatts(
                chargingProfile = null,
                transactionStartedAt = scheduleStart,
                now = scheduleStart,
            ),
        )
    }

    @Test
    fun `returns null when the profile carries no schedule`() {
        assertNull(
            ChargingProfileCalculator.getWatts(
                chargingProfile = ChargingProfile(chargingProfileId = 1, chargingSchedule = null),
                transactionStartedAt = scheduleStart,
                now = scheduleStart,
            ),
        )
    }

    @Test
    fun `returns null before the schedule starts`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            periods = listOf(period(startPeriod = 0, limit = 16.0)),
        )

        assertNull(
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart.minusSeconds(1),
            ),
        )
    }

    @Test
    fun `resolves the limit at the exact moment the schedule starts`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            periods = listOf(period(startPeriod = 0, limit = 16.0)),
        )

        assertEquals(
            watts16AThreePhase,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart,
            ),
        )
    }

    @Test
    fun `holds the last period open when the schedule has no duration`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            periods = listOf(period(startPeriod = 0, limit = 16.0)),
        )

        assertEquals(
            watts16AThreePhase,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart.plusSeconds(60 * 60 * 24),
            ),
        )
    }

    @Test
    fun `stops applying the profile once the schedule duration has elapsed`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            durationSeconds = 3600,
            periods = listOf(period(startPeriod = 0, limit = 16.0)),
        )

        assertNull(
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart.plusSeconds(3601),
            ),
        )
    }

    @Test
    fun `anchors a relative schedule to the transaction start`() {
        val profile = profileOf(
            startSchedule = null,
            periods = listOf(period(startPeriod = 0, limit = 16.0)),
        )

        assertEquals(
            watts16AThreePhase,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart,
            ),
        )
    }

    @Test
    fun `never returns less than the minimum charging rate`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            minChargingRate = 10.0,
            periods = listOf(period(startPeriod = 0, limit = 6.0)),
        )

        assertEquals(
            10.0 * 230.0 * 3,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart,
            ),
        )
    }

    @Test
    fun `leaves a limit above the minimum charging rate untouched`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            minChargingRate = 10.0,
            periods = listOf(period(startPeriod = 0, limit = 16.0)),
        )

        assertEquals(
            watts16AThreePhase,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart,
            ),
        )
    }

    @Test
    fun `scales the wattage by the number of phases`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            periods = listOf(period(startPeriod = 0, limit = 16.0, numberPhases = 1)),
        )

        assertEquals(
            16.0 * 230.0 * 1,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart,
            ),
        )
    }

    @Test
    fun `applies the period in force rather than the next one in a multi period schedule`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            periods = listOf(
                period(startPeriod = 3600, limit = 32.0),
                period(startPeriod = 0, limit = 16.0),
            ),
        )

        assertEquals(
            watts16AThreePhase,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart.plusSeconds(600),
            ),
            "at +600s the period in force is the 16 A one starting at 0s",
        )
    }

    @Test
    fun `steps up to the next period the moment its startPeriod elapses`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            periods = listOf(
                period(startPeriod = 0, limit = 16.0),
                period(startPeriod = 3600, limit = 32.0),
            ),
        )

        assertEquals(
            32.0 * 230.0 * 3,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart.plusSeconds(3600),
            ),
        )
    }

    @Test
    fun `keeps applying the profile part way through a schedule that declares a duration`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            durationSeconds = 3600,
            periods = listOf(period(startPeriod = 0, limit = 16.0)),
        )

        assertEquals(
            watts16AThreePhase,
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart.plusSeconds(600),
            ),
            "a bounded schedule still applies while it is running",
        )
    }

    @Test
    fun `returns null while the first period is still in the future`() {
        val profile = profileOf(
            startSchedule = scheduleStart,
            periods = listOf(period(startPeriod = 300, limit = 16.0)),
        )

        assertNull(
            ChargingProfileCalculator.getWatts(
                chargingProfile = profile,
                transactionStartedAt = scheduleStart,
                now = scheduleStart.plusSeconds(60),
            ),
            "no limit is in force until the first period begins",
        )
    }

    private fun profileOf(
        startSchedule: Instant?,
        periods: List<ChargingSchedulePeriod>,
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

    private fun period(
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
}
