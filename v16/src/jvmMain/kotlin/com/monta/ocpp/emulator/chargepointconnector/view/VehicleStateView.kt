package com.monta.ocpp.emulator.chargepointconnector.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.model.CarState
import com.monta.ocpp.emulator.common.components.getButtonStateColor
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.setConnectorCarState

@Composable
fun VehicleStateView(
    connector: ChargePointConnectorDAO
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CarState.entries.forEach { carState ->
            VehicleStateButton(
                connector = connector,
                carState = carState
            )
        }
    }
}

@Composable
fun RowScope.VehicleStateButton(
    connector: ChargePointConnectorDAO,
    carState: CarState
) {
    Button(
        modifier = Modifier.weight(1F)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = getButtonStateColor(carState == connector.carState)
        ),
        onClick = {
            launchThread {
                connector.setConnectorCarState(
                    carState = carState
                )
            }
        }
    ) {
        Text(
            text = carState.label
        )
    }
}
