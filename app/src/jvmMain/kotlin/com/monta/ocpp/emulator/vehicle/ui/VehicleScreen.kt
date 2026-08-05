package com.monta.ocpp.emulator.vehicle.ui

import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.designsystem.ui.component.DualColumView
import com.monta.ocpp.emulator.navigation.ui.PageScaffold

@Composable
internal fun VehicleScreen() {
    PageScaffold(
        title = "Vehicle Emulator",
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
