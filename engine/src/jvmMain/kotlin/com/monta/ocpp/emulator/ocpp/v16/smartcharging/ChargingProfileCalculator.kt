package com.monta.ocpp.emulator.ocpp.v16.smartcharging

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedule
import java.time.Duration
import java.time.Instant

object ChargingProfileCalculator {

    /**
     * Resolves the charging profile down to the wattage that should be drawn at [now].
     *
     * Takes values rather than a transaction so the maths stays independent of the database,
     * and takes [now] explicitly so it can be exercised at a fixed point in time.
     *
     * @param transactionStartedAt fallback schedule start, used when the schedule is relative
     *  (`startSchedule` is null) and is therefore anchored to the transaction.
     */
    fun getWatts(
        chargingProfile: ChargingProfile?,
        transactionStartedAt: Instant,
        now: Instant = Instant.now(),
    ): Double? {
        val (ampsPerPhase, phases) = getAmps(
            chargingProfile = chargingProfile,
            transactionStartedAt = transactionStartedAt,
            now = now,
        ) ?: return null
        return (ampsPerPhase * 230.0) * phases.toDouble()
    }

    private fun getAmps(
        chargingProfile: ChargingProfile?,
        transactionStartedAt: Instant,
        now: Instant,
    ): Pair<Double, Int>? {
        if (chargingProfile == null) {
            return null
        }

        val chargingSchedule: ChargingSchedule? = chargingProfile.chargingSchedule

        if (chargingSchedule == null) {
            return null
        }

        val scheduleStart = chargingSchedule.startSchedule?.toInstant() ?: transactionStartedAt

        // Check if our schedule has started yet
        if (now < scheduleStart) {
            // Our schedule isn't valid yet
            return null
        }

        // A schedule that declares a duration stops applying once that duration has elapsed
        val duration = chargingSchedule.duration

        if (duration != null && now > scheduleStart.plusSeconds(duration.toLong())) {
            return null
        }

        // v1.6 section 7.8: the period in force is the last one whose startPeriod has already
        // elapsed. Periods are relative to the schedule start and are not required to be ordered.
        val elapsedSeconds = Duration.between(scheduleStart, now).seconds
        val activePeriod = chargingSchedule.chargingSchedulePeriod
            .sortedBy { chargingSchedulePeriod -> chargingSchedulePeriod.startPeriod }
            .lastOrNull { chargingSchedulePeriod ->
                val startPeriod = chargingSchedulePeriod.startPeriod
                // startPeriod should never be null, but we have to check anyway
                startPeriod != null && startPeriod <= elapsedSeconds
            }

        // Nothing has come into force yet, the first period starts later than now
        if (activePeriod == null) {
            return null
        }

        val limit: Double? = activePeriod.limit

        // If we don't have a limit return null (again this shouldn't happen)
        if (limit == null) {
            return null
        }

        val minChargingRate = chargingSchedule.minChargingRate

        return if (minChargingRate != null) {
            // We should never return lower than our minChargingRate
            maxOf(limit, minChargingRate) to activePeriod.numberPhases
        } else {
            limit to activePeriod.numberPhases
        }
    }
}
