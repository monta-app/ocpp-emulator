package com.monta.ocpp.emulator.chargepointconnector.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.model.CarState
import com.monta.ocpp.emulator.common.components.SegmentedToggle
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.setConnectorCarState

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
