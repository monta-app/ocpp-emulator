package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Hover tooltip with the shadcn look: a small rounded near-black bubble with
 * white `body2` text.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextTooltip(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TooltipArea(
        modifier = modifier,
        tooltip = {
            Surface(
                modifier = Modifier.padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                color = Color(0xF218181B),
                contentColor = Color.White,
                elevation = 0.dp,
            ) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp,
                    ),
                    text = text,
                    style = MaterialTheme.typography.body2,
                )
            }
        },
        content = content,
    )
}
