package com.monta.ocpp.emulator.common.util

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedule
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedulePeriod
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
import java.time.Instant

object ChargingProfileCalculator {

    fun getWatts(
        transaction: ChargePointTransactionDAO
    ): Double? {
        val (ampsPerPhase, phases) = getAmps(transaction) ?: return null
        return (ampsPerPhase * 230.0) * phases.toDouble()
    }

    private fun getAmps(
        transaction: ChargePointTransactionDAO,
        chargingProfile: ChargingProfile? = transaction.chargingProfile
    ): Pair<Double, Int>? {
        if (chargingProfile == null) {
            return null
        }

        val chargingSchedule: ChargingSchedule? = chargingProfile.chargingSchedule

        if (chargingSchedule == null) {
            return null
        }

        val now = Instant.now()
        val scheduleStart = chargingSchedule.startSchedule?.toInstant() ?: transaction.createdAt
        val scheduleEnd: Instant? = chargingSchedule.duration?.let { duration ->
            scheduleStart.plusSeconds(duration.toLong())
        }

        // Check if our schedule has started yet
        if (now < scheduleStart) {
            // Our schedule isn't valid yet
            return null
        }

        // Ensure our periods are sorted by the start duration
        val sortedPeriods = chargingSchedule.chargingSchedulePeriod.sortedBy { it.startPeriod }

        // Iterate through our periods
        for (chargingSchedulePeriod in sortedPeriods) {
            val chargingLimit = chargingSchedulePeriod.checkAndGetLimit(
                scheduleStart = scheduleStart,
                minChargingRate = chargingSchedule.minChargingRate,
                checkDate = now
            )
            if (chargingLimit != null) {
                return chargingLimit
            }
        }
        // If we don't find a schedule above, we will revert to trying the last period in our schedule
        // But instead we will use the scheduleEnd as our check date, if it's null we use the last period
        // As described in the OCPP docs
        return sortedPeriods.lastOrNull()?.checkAndGetLimit(
            scheduleStart = scheduleStart,
            minChargingRate = chargingSchedule.minChargingRate,
            checkDate = scheduleEnd
        )
    }

    private fun ChargingSchedulePeriod.checkAndGetLimit(
        scheduleStart: Instant,
        minChargingRate: Double?,
        checkDate: Instant?
    ): Pair<Double, Int>? {
        // How many seconds from the schedule start does this period start at?
        val startPeriod = startPeriod?.toLong()
        // This should never be null, but we have to check anyway
        if (startPeriod == null) return null
        // Create a date so we can compare
        val periodStart = scheduleStart.plusSeconds(startPeriod)
        // If our current time is past the start time of this period
        if (checkDate != null && periodStart < checkDate) return null
        // If we have a valid period lets return that limit
        val limit: Double? = limit
        // If we don't have a limit return null (again this shouldn't happen)
        if (limit == null) return null
        // Otherwise we do our calculation based if we have a min charging rate
        return if (minChargingRate != null) {
            // If we do we should never return lower than our minChargingRate
            maxOf(limit, minChargingRate) to numberPhases
        } else {
            // Otherwise just return our limit
            limit to numberPhases
        }
    }
}
