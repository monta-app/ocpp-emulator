package com.monta.ocpp.emulator.vehicle.view

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.common.components.DualColumView
import com.monta.ocpp.emulator.interceptor.view.BasePage
import com.monta.ocpp.emulator.interceptor.view.BottomNavDestination

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
