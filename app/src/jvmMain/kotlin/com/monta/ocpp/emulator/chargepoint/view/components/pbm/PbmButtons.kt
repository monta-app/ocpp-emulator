package com.monta.ocpp.emulator.chargepoint.view.components.pbm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.common.components.OutlineButton
import com.monta.ocpp.emulator.common.components.PrimaryButton
import com.monta.ocpp.emulator.common.components.SectionCard

@Composable
fun pbmButtons() {
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PrimaryButton(
                onClick = {
                    PbmService.showSerialQR.value = false
                    PbmService.showUrlQR.value = true
                },
                modifier = Modifier.weight(1F),
            ) {
                Text("PBM QR")
            }
            OutlineButton(
                onClick = {
                    PbmService.showSerialQR.value = true
                    PbmService.showUrlQR.value = false
                },
                modifier = Modifier.weight(1F),
            ) {
                Text("Serial QR")
            }
        }
    }
}
