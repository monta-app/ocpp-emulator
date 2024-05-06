package com.monta.ocpp.emulator.chargepoint.view.components.pbm

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import kotlinx.coroutines.launch

@Composable
fun PbmDialog(
    chargePoint: ChargePointDAO
) {
    val coroutineScope = rememberCoroutineScope()

    var showUrlQR by remember {
        mutableStateOf(false)
    }

    var showSerialQR by remember {
        mutableStateOf(false)
    }

    coroutineScope.launch {
        PbmService.showUrlQR.collect {
            showUrlQR = it
        }
    }
    coroutineScope.launch {
        PbmService.showSerialQR.collect {
            showSerialQR = it
        }
    }

    if (!showUrlQR && !showSerialQR) {
        return
    }

    AlertDialog(
        title = {
            Text(
                text = if (showUrlQR) {
                    "URL QR"
                } else {
                    "Serial QR"
                }
            )
        },
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = {
                    PbmService.showUrlQR.value = false
                    PbmService.showSerialQR.value = false
                }
            ) {
                Text("Close")
            }
        },
        text = {
            Column {
                if (PbmService.showUrlQR.value) {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = "The following QR code should be use for starting the PBM flow via the phone's camera app",
                        style = MaterialTheme.typography.subtitle1
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = "The following QR code should be use in the PBM flow for registering the emulator's serial number",
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        modifier = Modifier.align(Alignment.Center)
                            .size(
                                width = 250.dp,
                                height = 250.dp
                            ),
                        bitmap = PbmService.createQrCode(
                            chargePoint = chargePoint,
                            showUrlQR = showUrlQR
                        ),
                        contentDescription = "Hello"
                    )
                }
            }
        }
    )
}
