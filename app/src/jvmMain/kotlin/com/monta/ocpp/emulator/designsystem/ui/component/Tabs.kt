package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * shadcn-style tabs: pill triggers sitting on a muted rounded strip
 * (`TabsList`/`TabsTrigger`); the selected trigger reads as a raised surface
 * card. Scrolls horizontally when the triggers outgrow the width.
 */
@Composable
fun <T> TabBar(
    tabs: List<T>,
    selected: T,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    onSelect: (T) -> Unit,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .clip(RoundedCornerShape(10.dp))
            .background(mutedSurfaceColor())
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            TabTrigger(
                text = label(tab),
                selected = tab == selected,
                onClick = {
                    onSelect(tab)
                },
            )
        }
    }
}

@Composable
private fun TabTrigger(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    // The selected pill must sit visually *above* the muted strip. In light
    // mode the plain surface (white) does that; in dark mode surface is darker
    // than the translucent strip and reads as a hole with a glowing rim, so a
    // translucent foreground fill is used instead (shadcn's dark bg-input/30).
    val selectedBackground = if (MaterialTheme.colors.isLight) {
        MaterialTheme.colors.surface
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(if (selected) selectedBackground else Color.Transparent)
            .then(
                // Hairline border so the selected pill stands out against the
                // page surface, which shares its colour (shadcn uses shadow-sm).
                if (selected) {
                    Modifier.border(
                        width = 1.dp,
                        color = cardBorderColor(),
                        shape = RoundedCornerShape(7.dp),
                    )
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 28.dp)
            .padding(
                horizontal = 12.dp,
                vertical = 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            color = if (selected) MaterialTheme.colors.onSurface else mutedForegroundColor(),
            maxLines = 1,
        )
    }
}
