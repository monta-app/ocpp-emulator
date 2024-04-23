package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.view.components.security.securityEventComponent
import com.monta.ocpp.emulator.common.components.getButtonStateColor
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.common.components.toReadable
import com.monta.ocpp.emulator.v16.setStatus
import kotlinx.coroutines.launch

@Composable
fun chargePointComponent(
    chargePoint: ChargePointDAO
) {
    val coroutineScope = rememberCoroutineScope()

    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = getCardStyle().fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = "Charge Point",
                    style = MaterialTheme.typography.h5
                )
            }

            Text(
                "Identity: ${chargePoint.identity}",
                modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(chargePoint.identity))
                }
            )
            Text("Status: ${chargePoint.status}")
            Text("Status At: ${chargePoint.statusAt.toReadable()}")
            Text("Firmware: ${chargePoint.firmware}")
            Text("Firmware Status: ${chargePoint.firmwareStatus}")
            Text("Diagnostic Status: ${chargePoint.diagnosticsStatus}")

            chargePointDisplayComponent(chargePoint)

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                arrayOf(ChargePointStatus.Available, ChargePointStatus.Unavailable)
                    .forEach { status ->
                        Button(
                            modifier = Modifier.weight(1F).fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = getButtonStateColor(status == chargePoint.status)
                            ),
                            onClick = {
                                coroutineScope.launch {
                                    chargePoint.setStatus(
                                        status = status
                                    )
                                }
                            }
                        ) {
                            Text("$status")
                        }
                    }
            }

            securityEventComponent(chargePoint)
        }
    }
}
