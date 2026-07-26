package com.monta.ocpp.emulator.chargepoint.view.components.pbm

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import com.monta.ocpp.emulator.common.components.AppDialog
import com.monta.ocpp.emulator.common.components.OutlineButton
import kotlinx.coroutines.launch

@Composable
fun PbmDialog(
    chargePoint: ChargePointDAO,
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

    AppDialog(
        onDismissRequest = {
            PbmService.showUrlQR.value = false
            PbmService.showSerialQR.value = false
        },
        title = if (showUrlQR) "URL QR" else "Serial QR",
        description = if (showUrlQR) {
            "Scan this code with your phone's camera to start the PBM flow."
        } else {
            "Use this code in the PBM flow to register the emulator's serial number."
        },
        dismissButton = {
            OutlineButton(
                onClick = {
                    PbmService.showUrlQR.value = false
                    PbmService.showSerialQR.value = false
                },
            ) {
                Text("Close")
            }
        },
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                modifier = Modifier.align(Alignment.Center)
                    .size(
                        width = 250.dp,
                        height = 250.dp,
                    ),
                bitmap = PbmService.createQrCode(
                    chargePoint = chargePoint,
                    showUrlQR = showUrlQR,
                ),
                contentDescription = null,
            )
        }
    }
}
