package com.monta.ocpp.emulator.chargepoint.view.components.security

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.common.components.AppDialog
import com.monta.ocpp.emulator.common.components.OutlineButton
import com.monta.ocpp.emulator.common.components.PrimaryButton
import com.monta.ocpp.emulator.common.components.Spinner
import com.monta.ocpp.emulator.common.components.mutedForegroundColor
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.ChargePointManager

@Composable
fun ColumnScope.securityEventComponent(
    chargePoint: ChargePointDAO,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var securityEvent by remember {
        mutableStateOf(SecurityEvent.TamperDetectionActivated)
    }

    var techInfo by remember {
        mutableStateOf("")
    }

    OutlineButton(
        onClick = {
            expanded = true
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Security Event")
    }

    if (expanded) {
        AppDialog(
            onDismissRequest = {
                expanded = false
            },
            title = "Security Event",
            description = "Send a security notification to the CSMS.",
            modifier = Modifier.width(440.dp),
            confirmButton = {
                PrimaryButton(
                    onClick = {
                        launchThread {
                            val chargePointManager: ChargePointManager by injectAnywhere()
                            chargePointManager.securityEvent(
                                chargePoint = chargePoint,
                                securityEvent = securityEvent,
                                techInfo = techInfo,
                            )
                        }
                        expanded = false
                    },
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                OutlineButton(
                    onClick = {
                        expanded = false
                    },
                ) {
                    Text("Close")
                }
            },
        ) {
            Spinner(
                modifier = Modifier.fillMaxWidth(),
                label = "Security Event",
                value = securityEvent,
                values = SecurityEvent.entries,
                render = { it.name },
                onSelectionChanged = { securityEvent = it },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = techInfo,
                label = {
                    Text("Tech Info (Optional)")
                },
                onValueChange = { newValue ->
                    techInfo = newValue
                },
            )
            Text(
                text = securityEvent.description,
                style = MaterialTheme.typography.body2,
                color = mutedForegroundColor(),
            )
        }
    }
}
