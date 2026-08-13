package com.monta.ocpp.emulator.chargepoint.core.ui.component

import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.designsystem.ui.component.MontaStateIcon
import com.monta.ocpp.emulator.designsystem.ui.component.TextTooltip
import com.monta.ocpp.emulator.ocpp.connection.ProtocolConnectionManager
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere

/**
 * Displays the state of a charge point,
 * and allows the user to connect or disconnect the charge point quickly
 */
@Composable
fun ChargePointConnectionButton(
    chargePoint: ChargePointDAO,
    modifier: Modifier = Modifier,
) {
    val connectionManager: ProtocolConnectionManager by injectAnywhere()

    TextTooltip(
        modifier = modifier,
        text = if (chargePoint.connected) {
            "Disconnect charge point"
        } else {
            "Connect charge point"
        },
    ) {
        IconButton(
            onClick = {
                if (chargePoint.connected) {
                    connectionManager.disconnect(chargePoint.idValue)
                } else {
                    connectionManager.connect(chargePoint.idValue)
                }
            },
        ) {
            MontaStateIcon(
                state = chargePoint.connected,
                onState = "stop_circle",
                offState = "play_circle",
            )
        }
    }
}
