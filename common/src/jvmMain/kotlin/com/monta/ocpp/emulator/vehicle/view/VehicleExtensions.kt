package com.monta.ocpp.emulator.vehicle.view

import com.monta.ocpp.emulator.common.util.PrettyYamlFormatter
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.vehicle.model.EnodeVehicle
import kotlinx.coroutines.runBlocking
import java.time.Instant

internal fun parseVehicleCatching(
    vehicleYaml: String,
    vehiclePayload: EnodeVehicle,
): EnodeVehicle {
    val vehicleLogger: VehicleLogger by injectAnywhere()

    return try {
        PrettyYamlFormatter.readYaml(
            yaml = vehicleYaml,
            clazz = EnodeVehicle::class.java,
        )
    } catch (e: Exception) {
        runBlocking {
            vehicleLogger.warn(message = "parsing vehicle yaml failed")
        }
        vehiclePayload
    }
}

internal fun EnodeVehicle.withValues(
    externalVehicleId: String,
    soc: Double,
): EnodeVehicle {
    val now = Instant.now()
    return this.copy(
        id = externalVehicleId,
        lastSeen = now,
        chargeState = this.chargeState?.copy(
            batteryLevel = soc.toInt(),
            lastUpdated = Instant.now(),
        ),
        odometer = this.odometer?.copy(
            lastUpdated = Instant.now(),
        ),
        location = this.location?.copy(
            lastUpdated = Instant.now(),
        ),
    )
}

internal fun defaultVehicle(
    externalVehicleId: String,
    soc: Double,
): EnodeVehicle {
    val now = Instant.now()

    return EnodeVehicle(
        id = externalVehicleId,
        userId = "montaTestUser",
        vendor = "monta",
        isReachable = true,
        lastSeen = now,
        chargeState = EnodeVehicle.ChargeState(
            isPluggedIn = true,
            isCharging = true,
            batteryLevel = soc.toInt(),
            range = 100,
            batteryCapacity = 60000.0,
            chargeLimit = 100,
            chargeRate = 10.0,
            chargeTimeRemaining = 10,
            isFullyCharged = false,
            lastUpdated = now,
            powerDeliveryState = "PLUGGED_IN:CHARGING",
            maxCurrent = 32,
        ),
        information = EnodeVehicle.Information(
            brand = "Monta",
            model = "Monster Truck",
            year = 2023,
            vin = "MNT12345678912345",
        ),
        odometer = EnodeVehicle.Odometer(
            distance = 9000.1,
            lastUpdated = now,
        ),
        location = EnodeVehicle.Location(
            latitude = 34.165152,
            longitude = -118.281788,
            lastUpdated = now,
        ),
        locationId = null,
        scopes = emptyList(),
    )
}
