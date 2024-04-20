package com.monta.ocpp.emulator.v16.view.chargepoint.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.common.components.Spinner
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.data.entity.ChargePointDAO
import com.monta.ocpp.emulator.v16.service.ocpp.ChargePointManager

@Composable
fun ColumnScope.securityEventComponent(
    chargePoint: ChargePointDAO
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

    Button(
        onClick = {
            expanded = true
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Security Event")
    }

    if (expanded) {
        AlertDialog(
            title = {
                Text("Security Event")
            },
            onDismissRequest = {
                expanded = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        launchThread {
                            val chargePointManager: ChargePointManager by injectAnywhere()
                            chargePointManager.securityEvent(
                                chargePoint = chargePoint,
                                securityEvent = securityEvent,
                                techInfo = techInfo
                            )
                        }
                        expanded = false
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        expanded = false
                    }
                ) {
                    Text("Close")
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spinner(
                        label = "Security Event",
                        value = securityEvent,
                        values = SecurityEvent.entries,
                        render = { it.name },
                        onSelectionChanged = { securityEvent = it }
                    )
                    OutlinedTextField(
                        value = techInfo,
                        label = {
                            Text("Tech Info (Optional)")
                        },
                        onValueChange = { newValue ->
                            techInfo = newValue
                        }
                    )
                    Text(securityEvent.description)
                }
            }
        )
    }
}
