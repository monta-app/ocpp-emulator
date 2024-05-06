package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO

@Composable
fun chargePointDisplayComponent(
    chargePoint: ChargePointDAO
) {
    Text(
        text = "Display",
        modifier = Modifier.padding(top = 8.dp),
        fontWeight = FontWeight.Bold
    )
    Card(
        modifier = Modifier.shadow(0.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp))
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = chargePoint.displayText,
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 5
            )
        }
    }
}
