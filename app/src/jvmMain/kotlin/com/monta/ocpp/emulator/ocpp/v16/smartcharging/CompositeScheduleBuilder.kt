package com.monta.ocpp.emulator.ocpp.v16.smartcharging

import com.monta.library.ocpp.common.chargingprofile.ChargingRateUnit
import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedule
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedulePeriod
import java.time.ZonedDateTime
import kotlin.math.min

object CompositeScheduleBuilder {

    fun build(
        profiles: List<ChargingProfile>,
        durationSeconds: Int,
        scheduleStart: ZonedDateTime,
        preferredUnit: ChargingRateUnit,
        fallbackMaxWatts: Double,
    ): ChargingSchedule? {
        if (durationSeconds <= 0) {
            return null
        }

        val boundaries = sortedSetOf(0, durationSeconds)
        for (profile in profiles) {
            val schedule = profile.chargingSchedule ?: continue
            for (period in schedule.chargingSchedulePeriod) {
                val start = period.startPeriod ?: continue
                if (start in 0 until durationSeconds) {
                    boundaries.add(start)
                }
            }
        }

        val boundaryList = boundaries.toList()
        val compositePeriods = mutableListOf<ChargingSchedulePeriod>()

        for (index in 0 until boundaryList.lastIndex) {
            val periodStart = boundaryList[index]
            val periodEnd = boundaryList[index + 1]
            if (periodStart >= durationSeconds) {
                break
            }
            val midpoint = periodStart + ((periodEnd - periodStart) / 2)
            val limitWatts = resolveLimitWatts(
                profiles = profiles,
                offsetSeconds = midpoint,
                scheduleStart = scheduleStart,
                fallbackMaxWatts = fallbackMaxWatts,
            )
            val limit = when (preferredUnit) {
                ChargingRateUnit.A -> limitWatts / 230.0 / 3.0
                ChargingRateUnit.W, ChargingRateUnit.VAR -> limitWatts
            }
            compositePeriods.add(
                ChargingSchedulePeriod(
                    startPeriod = periodStart,
                    limit = limit,
                    numberPhases = 3,
                ),
            )
        }

        if (compositePeriods.isEmpty()) {
            val limit = when (preferredUnit) {
                ChargingRateUnit.A -> fallbackMaxWatts / 230.0 / 3.0
                ChargingRateUnit.W, ChargingRateUnit.VAR -> fallbackMaxWatts
            }
            compositePeriods.add(
                ChargingSchedulePeriod(
                    startPeriod = 0,
                    limit = limit,
                    numberPhases = 3,
                ),
            )
        }

        return ChargingSchedule(
            duration = durationSeconds,
            startSchedule = scheduleStart,
            chargingRateUnit = preferredUnit,
            chargingSchedulePeriod = compositePeriods,
        )
    }

    private fun resolveLimitWatts(
        profiles: List<ChargingProfile>,
        offsetSeconds: Int,
        scheduleStart: ZonedDateTime,
        fallbackMaxWatts: Double,
    ): Double {
        var limit = fallbackMaxWatts
        for (profile in profiles.sortedByDescending { it.stackLevel ?: 0 }) {
            val schedule = profile.chargingSchedule ?: continue
            val periodLimit = periodLimitAt(schedule, offsetSeconds) ?: continue
            val asWatts = when (schedule.chargingRateUnit) {
                ChargingRateUnit.A -> periodLimit * 230.0 * 3.0
                ChargingRateUnit.W, ChargingRateUnit.VAR, null -> periodLimit
            }
            limit = min(limit, asWatts)
        }
        return limit
    }

    private fun periodLimitAt(
        schedule: ChargingSchedule,
        offsetSeconds: Int,
    ): Double? {
        val sorted = schedule.chargingSchedulePeriod.sortedBy { it.startPeriod ?: 0 }
        var active: ChargingSchedulePeriod? = null
        for (period in sorted) {
            val start = period.startPeriod ?: continue
            if (start <= offsetSeconds) {
                active = period
            } else {
                break
            }
        }
        return active?.limit
    }
}
