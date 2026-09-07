package com.monta.ocpp.emulator.ocpp.v16.smartcharging

import com.monta.library.ocpp.common.chargingprofile.ChargingProfileKind
import com.monta.library.ocpp.common.chargingprofile.ChargingRateUnit
import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingProfilePurposeType
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedule
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedulePeriod
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CompositeScheduleBuilderTest {
    @Test
    fun `builds schedule from stacked profiles`() {
        val start = ZonedDateTime.parse("2026-01-01T00:00:00Z")
        val profile = ChargingProfile(
            chargingProfileId = 1,
            stackLevel = 0,
            chargingProfilePurpose = ChargingProfilePurposeType.ChargePointMaxProfile,
            chargingProfileKind = ChargingProfileKind.Absolute,
            chargingSchedule = ChargingSchedule(
                duration = 3600,
                startSchedule = start,
                chargingRateUnit = ChargingRateUnit.W,
                chargingSchedulePeriod = listOf(
                    ChargingSchedulePeriod(startPeriod = 0, limit = 11000.0, numberPhases = 3),
                    ChargingSchedulePeriod(startPeriod = 1800, limit = 7000.0, numberPhases = 3),
                ),
            ),
        )

        val schedule = CompositeScheduleBuilder.build(
            profiles = listOf(profile),
            durationSeconds = 3600,
            scheduleStart = start,
            preferredUnit = ChargingRateUnit.W,
            fallbackMaxWatts = 22000.0,
        )

        assertNotNull(schedule)
        assertEquals(3600, schedule.duration)
        assertTrue(schedule.chargingSchedulePeriod.isNotEmpty())
        assertEquals(0, schedule.chargingSchedulePeriod.first().startPeriod)
    }

    @Test
    fun `rejects non-positive duration`() {
        val schedule = CompositeScheduleBuilder.build(
            profiles = emptyList(),
            durationSeconds = 0,
            scheduleStart = ZonedDateTime.now(),
            preferredUnit = ChargingRateUnit.W,
            fallbackMaxWatts = 11000.0,
        )
        assertEquals(null, schedule)
    }
}
