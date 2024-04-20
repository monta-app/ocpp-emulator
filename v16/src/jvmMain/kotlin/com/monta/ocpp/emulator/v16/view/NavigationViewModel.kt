package com.monta.ocpp.emulator.v16.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.monta.ocpp.emulator.v16.data.entity.ChargePointDAO
import org.koin.core.annotation.Singleton

@Singleton
class NavigationViewModel {

    var windowHasFocus: Boolean by mutableStateOf(true)

    var currentScreen: Screen by mutableStateOf(Screen.ChargePoints)

    var lastActiveChargePointId: Long? by mutableStateOf(null)

    fun navigateTo(screen: Screen) {
        currentScreen = screen
        if (screen is Screen.ChargePoint) {
            lastActiveChargePointId = screen.chargePointId
        }
    }

    fun chargePointsScreen() {
        currentScreen = Screen.ChargePoints
    }

    fun getChargePointId(): Long {
        return (currentScreen as Screen.ChargePoint).chargePointId
    }

    fun getChargePointToEdit(): ChargePointDAO? {
        return (currentScreen as Screen.CreateChargePoint).chargePoint
    }

    sealed class Screen {
        data object ChargePoints : Screen()
        data class CreateChargePoint(
            val chargePoint: ChargePointDAO? = null
        ) : Screen()

        data class ChargePoint(
            val chargePointId: Long
        ) : Screen()

        data object Vehicles : Screen()
    }
}
