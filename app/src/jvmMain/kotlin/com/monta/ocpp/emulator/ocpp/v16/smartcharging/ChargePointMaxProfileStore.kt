package com.monta.ocpp.emulator.ocpp.v16.smartcharging

import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

@Singleton
class ChargePointMaxProfileStore {
    private val profiles = ConcurrentHashMap<Long, ChargingProfile>()

    fun put(
        chargePointId: Long,
        profile: ChargingProfile,
    ) {
        profiles[chargePointId] = profile
    }

    fun get(
        chargePointId: Long,
    ): ChargingProfile? = profiles[chargePointId]

    fun clear(
        chargePointId: Long,
        chargingProfileId: Int? = null,
        stackLevel: Int? = null,
    ) {
        val existing = profiles[chargePointId] ?: return
        if (chargingProfileId != null && existing.chargingProfileId != chargingProfileId) {
            return
        }
        if (stackLevel != null && existing.stackLevel != stackLevel) {
            return
        }
        profiles.remove(chargePointId)
    }
}
