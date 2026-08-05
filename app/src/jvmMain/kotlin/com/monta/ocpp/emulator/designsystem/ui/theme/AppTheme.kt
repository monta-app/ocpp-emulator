package com.monta.ocpp.emulator.designsystem.ui.theme

enum class AppTheme {
    Auto,
    Light,
    Dark,
    ;

    companion object {
        fun parse(
            value: String?,
        ): AppTheme {
            return entries.find { appTheme ->
                appTheme.name.equals(value, true)
            } ?: Auto
        }
    }
}
