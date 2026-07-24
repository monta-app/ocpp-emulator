package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.common.components.SectionLabel
import com.monta.ocpp.emulator.common.components.cardBorderColor
import com.monta.ocpp.emulator.common.components.mutedForegroundColor
import com.monta.ocpp.emulator.common.components.mutedSurfaceColor

@Composable
fun chargePointDisplayComponent(
    chargePoint: ChargePointDAO,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionLabel(
            text = "Display",
        )
        // Emulates the charge point's LCD, so a monospace terminal block reads
        // true to life and keeps the five fixed lines aligned.
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = mutedSurfaceColor(),
            border = BorderStroke(1.dp, cardBorderColor()),
            elevation = 0.dp,
        ) {
            Text(
                text = chargePoint.displayText,
                modifier = Modifier.fillMaxWidth()
                    .padding(12.dp),
                fontFamily = FontFamily.Monospace,
                color = mutedForegroundColor(),
                minLines = 5,
                maxLines = 5,
            )
        }
    }
}
