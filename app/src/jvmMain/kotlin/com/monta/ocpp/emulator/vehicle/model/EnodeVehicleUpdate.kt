package com.monta.ocpp.emulator.vehicle.model

import java.time.Instant

data class EnodeVehicleUpdate(
    val event: String,
    val createdAt: Instant,
    val user: MontaApiUser,
    val vehicle: EnodeVehicle,
)
