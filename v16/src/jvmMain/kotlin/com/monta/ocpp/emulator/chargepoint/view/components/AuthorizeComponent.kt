package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.common.components.RfidButton
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.ChargePointManager

@Composable
fun BoxScope.authorizeComponent(
    connector: ChargePointConnectorDAO
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var idTag by remember {
        mutableStateOf("")
    }

    RfidButton(
        modifier = Modifier.align(Alignment.CenterEnd)
            .size(32.dp)
    ) {
        expanded = true
    }

    if (expanded) {
        AlertDialog(
            title = {
                Text("Authorize")
            },
            onDismissRequest = {
                expanded = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        launchThread {
                            val chargePointManager: ChargePointManager by injectAnywhere()
                            chargePointManager.authorize(
                                connector = connector,
                                idTag = idTag
                            )
                            idTag = ""
                            expanded = false
                        }
                    }
                ) {
                    Text("Authorize")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        idTag = ""
                        expanded = false
                    }
                ) {
                    Text("Close")
                }
            },
            text = {
                Column {
                    Spacer(
                        modifier = Modifier.padding(8.dp)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = idTag,
                        label = {
                            Text("RFID Card Number")
                        },
                        onValueChange = { newIdTag ->
                            idTag = newIdTag
                        }
                    )
                }
            }
        )
    }
}
