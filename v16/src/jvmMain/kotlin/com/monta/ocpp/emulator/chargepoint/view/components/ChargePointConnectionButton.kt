package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.common.components.MontaStateIcon
import com.monta.ocpp.emulator.common.components.TextTooltip
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.v16.connection.ConnectionManager

/**
 * Displays the state of a charge point,
 * and allows the user to connect or disconnect the charge point quickly
 */
@Composable
fun ChargePointConnectionButton(
    chargePoint: ChargePointDAO,
    modifier: Modifier = Modifier
) {
    val connectionManager: ConnectionManager by injectAnywhere()

    TextTooltip(
        modifier = modifier,
        text = if (chargePoint.connected) {
            "Disconnect charge point"
        } else {
            "Connect charge point"
        }
    ) {
        IconButton(
            onClick = {
                if (chargePoint.connected) {
                    connectionManager.disconnect(chargePoint.idValue)
                } else {
                    connectionManager.connect(chargePoint.idValue)
                }
            }
        ) {
            MontaStateIcon(
                state = chargePoint.connected,
                onState = "stop_circle",
                offState = "play_circle"
            )
        }
    }
}
