package com.monta.ocpp.emulator.chargepoint.connector.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.designsystem.ui.component.SegmentedToggle
import com.monta.ocpp.emulator.ocpp.v16.extension.setConnectorCarState
import com.monta.ocpp.emulator.platform.util.launchThread

@Composable
fun VehicleStateView(
    connector: ChargePointConnectorDAO,
) {
    SegmentedToggle(
        options = CarState.entries,
        selected = connector.carState,
        label = { it.label },
        modifier = Modifier,
        onSelect = { carState ->
            launchThread {
                connector.setConnectorCarState(
                    carState = carState,
                )
            }
        },
    )
}
