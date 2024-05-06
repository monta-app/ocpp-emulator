package com.monta.ocpp.emulator.chargepointconnector.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.common.components.Spinner
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.setStatus

@Composable
fun ColumnScope.ConnectorStateView(
    connector: ChargePointConnectorDAO
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var connectorStatus by remember {
        mutableStateOf(connector.status)
    }

    var errorCode by remember {
        mutableStateOf(connector.errorCode)
    }

    Button(
        onClick = {
            expanded = true
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Connector Status")
    }

    if (expanded) {
        AlertDialog(
            title = {
                Text("Connector Status")
            },
            onDismissRequest = {
                expanded = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        launchThread {
                            connector.setStatus(
                                status = connectorStatus,
                                errorCode = errorCode
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
                        label = "Connector Status",
                        value = connectorStatus,
                        values = ChargePointStatus.entries,
                        render = { chargePointStatus ->
                            chargePointStatus.name
                        },
                        onSelectionChanged = { newChargePointStatus ->
                            connectorStatus = newChargePointStatus
                        }
                    )
                    Spinner(
                        label = "Error Code",
                        value = errorCode,
                        values = ChargePointErrorCode.entries,
                        render = { chargePointErrorCode ->
                            chargePointErrorCode.name
                        },
                        onSelectionChanged = { newChargePointErrorCode ->
                            errorCode = newChargePointErrorCode
                        }
                    )
                }
            }
        )
    }
}
