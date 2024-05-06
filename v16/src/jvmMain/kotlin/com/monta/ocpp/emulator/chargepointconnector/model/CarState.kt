package com.monta.ocpp.emulator.chargepointconnector.model

enum class CarState(
    val label: String,
    val helpString: String
) {
    A(
        label = "Unplugged",
        helpString = "Car is unplugged from the charge point"
    ),
    B(
        label = "Plugged",
        helpString = "Car is plugged in but not accepting electricity"
    ),
    C(
        label = "Ready",
        helpString = "Car is ready to receive electricity"
    )
}
