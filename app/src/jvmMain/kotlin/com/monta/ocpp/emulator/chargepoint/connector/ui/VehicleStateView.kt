package com.monta.ocpp.emulator.chargepoint.connector.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.designsystem.ui.component.SegmentedToggle
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointConnectorSummary
import com.monta.ocpp.emulator.ocpp.core.service.EmulatorEngine
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.platform.util.launchThread

@Composable
fun VehicleStateView(
    connector: ChargePointConnectorSummary,
) {
    val emulatorEngine: EmulatorEngine by injectAnywhere()

    SegmentedToggle(
        options = CarState.entries,
        selected = connector.carState,
        label = { it.label },
        modifier = Modifier,
        onSelect = { carState ->
            launchThread {
                emulatorEngine.setConnectorCarState(
                    connectorId = connector.id,
                    carState = carState,
                )
            }
        },
    )
}
