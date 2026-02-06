package com.monta.ocpp.emulator.vehicle.model

import java.time.Instant

data class EnodeVehicle(
    val id: String,
    val userId: String,
    val vendor: String,
    val isReachable: Boolean?,
    val lastSeen: Instant?,
    val chargeState: ChargeState?,
    val information: Information?,
    val odometer: Odometer?,
    val location: Location?,
    val scopes: List<String>,
    val locationId: String?,
) {
    data class Information(
        val brand: String?,
        val model: String?,
        val year: Int?,
        val vin: String?,
    )

    data class Odometer(
        val distance: Double?,
        val lastUpdated: Instant?,
    )

    data class ChargeState(
        val isPluggedIn: Boolean?,
        val isCharging: Boolean?,
        val batteryLevel: Int?,
        val range: Int?,
        val batteryCapacity: Double?,
        val chargeLimit: Int?,
        val chargeRate: Double?,
        val chargeTimeRemaining: Int?,
        val isFullyCharged: Boolean?,
        val lastUpdated: Instant?,
        val powerDeliveryState: String,
        val maxCurrent: Int?,
    )

    data class Location(
        val latitude: Double?,
        val longitude: Double?,
        val lastUpdated: Instant?,
    )
}
