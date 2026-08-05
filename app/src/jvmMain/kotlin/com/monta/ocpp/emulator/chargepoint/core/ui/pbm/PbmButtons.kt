package com.monta.ocpp.emulator.chargepoint.core.ui.pbm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.designsystem.ui.component.OutlineButton
import com.monta.ocpp.emulator.designsystem.ui.component.PrimaryButton
import com.monta.ocpp.emulator.designsystem.ui.component.SectionCard

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
                    PbmViewModel.showSerialQR.value = false
                    PbmViewModel.showUrlQR.value = true
                },
                modifier = Modifier.weight(1F),
            ) {
                Text("PBM QR")
            }
            OutlineButton(
                onClick = {
                    PbmViewModel.showSerialQR.value = true
                    PbmViewModel.showUrlQR.value = false
                },
                modifier = Modifier.weight(1F),
            ) {
                Text("Serial QR")
            }
        }
    }
}
