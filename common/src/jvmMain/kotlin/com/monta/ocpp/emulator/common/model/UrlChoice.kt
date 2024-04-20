package com.monta.ocpp.emulator.common.model

enum class UrlChoice(
    val ocppUrl: String,
    val apiUrl: String,
    val vehicleServiceUrl: String
) {
    Local(
        ocppUrl = "ws://localhost:8000",
        apiUrl = "",
        vehicleServiceUrl = ""
    ),
    Dev(
        ocppUrl = "wss://ocpp.dev.monta.app",
        apiUrl = "https://app.dev.monta.app",
        vehicleServiceUrl = "https://vehicles.dev.monta.app"
    ),
    Staging(
        ocppUrl = "wss://ocpp.staging.monta.app",
        apiUrl = "https://app.staging.monta.app",
        vehicleServiceUrl = "https://vehicles.staging.monta.app"

    ),
    Production(
        ocppUrl = "wss://ocpp.monta.app",
        apiUrl = "https://app.monta.app",
        vehicleServiceUrl = "https://vehicles.monta.app"
    ),
    Other(
        ocppUrl = "",
        apiUrl = "",
        vehicleServiceUrl = ""
    );

    companion object {
        fun fromUrl(url: String?): UrlChoice {
            return entries.find { it.ocppUrl == url } ?: Other
        }

        fun fromVehicleServiceUrl(vehicleServiceUrl: String?): UrlChoice {
            return entries.find { it.vehicleServiceUrl == vehicleServiceUrl } ?: Other
        }
    }
}
