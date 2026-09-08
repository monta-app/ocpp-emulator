package com.monta.ocpp.emulator.chargepoint.core.ui.component

import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monta.ocpp.emulator.designsystem.ui.component.MontaStateIcon
import com.monta.ocpp.emulator.designsystem.ui.component.TextTooltip
import com.monta.ocpp.emulator.ocpp.core.service.EmulatorEngine
import com.monta.ocpp.emulator.platform.util.injectAnywhere

/**
 * Displays the state of a charge point,
 * and allows the user to connect or disconnect the charge point quickly
 */
@Composable
fun ChargePointConnectionButton(
    chargePointId: Long,
    connected: Boolean,
    modifier: Modifier = Modifier,
) {
    val emulatorEngine: EmulatorEngine by injectAnywhere()

    TextTooltip(
        modifier = modifier,
        text = if (connected) {
            "Disconnect charge point"
        } else {
            "Connect charge point"
        },
    ) {
        IconButton(
            onClick = {
                if (connected) {
                    emulatorEngine.disconnect(chargePointId)
                } else {
                    emulatorEngine.connect(chargePointId)
                }
            },
        ) {
            MontaStateIcon(
                state = connected,
                onState = "stop_circle",
                offState = "play_circle",
            )
        }
    }
}
