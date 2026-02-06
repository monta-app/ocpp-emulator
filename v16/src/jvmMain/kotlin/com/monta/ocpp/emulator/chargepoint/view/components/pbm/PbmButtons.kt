package com.monta.ocpp.emulator.chargepoint.view.components.pbm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun pbmButtons() {
    Card {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth().weight(1F),
                onClick = {
                    PbmService.showSerialQR.value = false
                    PbmService.showUrlQR.value = true
                },
            ) {
                Text("PBM QR")
            }
            Button(
                modifier = Modifier.fillMaxWidth().weight(1F),
                onClick = {
                    PbmService.showSerialQR.value = true
                    PbmService.showUrlQR.value = false
                },
            ) {
                Text("Serial QR")
            }
        }
    }
}
