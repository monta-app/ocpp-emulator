package com.monta.ocpp.emulator.vehicle.ui

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.designsystem.ui.component.DualColumView
import com.monta.ocpp.emulator.interceptor.ui.BasePage
import com.monta.ocpp.emulator.interceptor.ui.BottomNavDestination

@Composable
internal fun VehicleScreen() {
    BasePage(
        selectedDestination = BottomNavDestination.Vehicles,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Vehicle Emulator")
                },
            )
        },
    ) {
        DualColumView(
            firstColumn = {
                VehicleView()
            },
            secondColumn = {
                VehicleLogView()
            },
        )
    }
}
