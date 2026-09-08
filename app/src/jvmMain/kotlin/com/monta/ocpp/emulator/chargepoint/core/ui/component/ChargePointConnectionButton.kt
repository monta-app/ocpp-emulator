package com.monta.ocpp.emulator.chargepoint.core.ui.component

import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monta.ocpp.emulator.designsystem.ui.component.MontaStateIcon
import com.monta.ocpp.emulator.designsystem.ui.component.TextTooltip
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointSummary
import com.monta.ocpp.emulator.ocpp.core.service.EmulatorEngine
import com.monta.ocpp.emulator.platform.util.injectAnywhere

/**
 * Displays the state of a charge point,
 * and allows the user to connect or disconnect the charge point quickly
 */
@Composable
fun ChargePointConnectionButton(
    chargePoint: ChargePointSummary,
    modifier: Modifier = Modifier,
) {
    val emulatorEngine: EmulatorEngine by injectAnywhere()

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
                    emulatorEngine.disconnect(chargePoint.id)
                } else {
                    emulatorEngine.connect(chargePoint.id)
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
