package com.monta.ocpp.emulator.v16.view.vehicle

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.common.components.DualColumView
import com.monta.ocpp.emulator.v16.view.util.BasePage
import com.monta.ocpp.emulator.vehicle.view.VehicleLogView
import com.monta.ocpp.emulator.vehicle.view.VehicleView

@Composable
internal fun VehicleScreen() {
    BasePage(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Vehicle Emulator")
                }
            )
        }
    ) {
        DualColumView(
            firstColumn = {
                VehicleView()
            },
            secondColumn = {
                VehicleLogView()
            }
        )
    }
}
