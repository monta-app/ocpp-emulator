package com.monta.ocpp.emulator.chargepoint.core.ui.pbm

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
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.designsystem.ui.component.AppDialog
import com.monta.ocpp.emulator.designsystem.ui.component.OutlineButton
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
        PbmViewModel.showUrlQR.collect {
            showUrlQR = it
        }
    }
    coroutineScope.launch {
        PbmViewModel.showSerialQR.collect {
            showSerialQR = it
        }
    }

    if (!showUrlQR && !showSerialQR) {
        return
    }

    AppDialog(
        onDismissRequest = {
            PbmViewModel.showUrlQR.value = false
            PbmViewModel.showSerialQR.value = false
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
                    PbmViewModel.showUrlQR.value = false
                    PbmViewModel.showSerialQR.value = false
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
                bitmap = PbmViewModel.createQrCode(
                    chargePoint = chargePoint,
                    showUrlQR = showUrlQR,
                ),
                contentDescription = null,
            )
        }
    }
}
