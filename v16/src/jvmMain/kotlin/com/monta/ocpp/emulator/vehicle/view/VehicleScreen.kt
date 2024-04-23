package com.monta.ocpp.emulator.vehicle.view

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.common.components.DualColumView
import com.monta.ocpp.emulator.interceptor.view.BasePage

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
