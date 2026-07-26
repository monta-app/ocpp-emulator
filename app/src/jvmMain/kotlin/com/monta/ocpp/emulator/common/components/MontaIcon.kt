package com.monta.ocpp.emulator.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MontaIcon(
    iconName: String,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    tooltipText: String? = null,
) {
    if (tooltipText != null) {
        TextTooltip(tooltipText) {
            Icon(
                painter = svgPainterResource("icons/$iconName.svg"),
                contentDescription = contentDescription,
                modifier = modifier.size(16.dp),
                tint = tint,
            )
        }
    } else {
        Icon(
            painter = svgPainterResource("icons/$iconName.svg"),
            contentDescription = contentDescription,
            modifier = modifier.size(16.dp),
            tint = tint,
        )
    }
}

@Composable
fun MontaStateIcon(
    state: Boolean,
    onState: String,
    offState: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
) {
    Icon(
        painter = if (state) {
            svgPainterResource("icons/$onState.svg")
        } else {
            svgPainterResource("icons/$offState.svg")
        },
        contentDescription = "",
        modifier = modifier.size(24.dp),
        tint = tint,
    )
}
