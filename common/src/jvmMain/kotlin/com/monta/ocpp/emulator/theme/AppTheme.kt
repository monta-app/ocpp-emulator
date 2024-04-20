package com.monta.ocpp.emulator.theme

enum class AppTheme {
    Auto,
    Light,
    Dark;

    companion object {
        fun parse(value: String?): AppTheme {
            return entries.find { appTheme ->
                appTheme.name.equals(value, true)
            } ?: Auto
        }
    }
}
