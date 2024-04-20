package com.monta.ocpp.emulator.common.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextTooltip(
    text: String,
    content: @Composable () -> Unit
) {
    TooltipArea(
        tooltip = {
            Card(
                modifier = Modifier.padding(8.dp),
                backgroundColor = Color.Black
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = text,
                    color = Color.White
                )
            }
        },
        content = content
    )
}
