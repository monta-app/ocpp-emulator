package com.monta.ocpp.emulator.v201

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.koin.core.annotation.Singleton

@Singleton
class NavigationViewModel {

    var windowHasFocus: Boolean by mutableStateOf(true)

    var currentScreen: Screen by mutableStateOf(Screen.ChargePoints)

    sealed class Screen {
        data object ChargePoints : Screen()
    }
}
