package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * shadcn-style select: a trigger that matches [InputField]'s look (36dp,
 * hairline border turning primary while open, muted chevron) opening a
 * width-matched popup of compact options, the selected one marked with a check.
 */
@Composable
fun <T> Select(
    modifier: Modifier = Modifier,
    label: String,
    value: T,
    values: List<T>,
    render: (T) -> String,
    enabled: Boolean = true,
    onSelectionChanged: (selection: T) -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var triggerWidthPx by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = modifier.alpha(if (enabled) 1f else 0.5f),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        triggerWidthPx = coordinates.size.width
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = if (expanded) MaterialTheme.colors.primary else cardBorderColor(),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clickable(enabled = enabled) {
                        expanded = true
                    }
                    .defaultMinSize(minHeight = 36.dp)
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp,
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = render(value),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = mutedForegroundColor(),
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.defaultMinSize(
                    minWidth = with(LocalDensity.current) { triggerWidthPx.toDp() },
                ),
            ) {
                values.forEach { entry ->
                    SelectItem(
                        text = render(entry),
                        selected = entry == value,
                        onClick = {
                            onSelectionChanged(entry)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(
                horizontal = 8.dp,
                vertical = 6.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.body2,
            maxLines = 1,
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                modifier = Modifier.size(16.dp),
                tint = mutedForegroundColor(),
            )
        }
    }
}

@Preview
@Composable
private fun Select_Preview() {
    MaterialTheme {
        val entry1 = Pair("Key1", "Entry1")
        val entry2 = Pair("Key2", "Entry2")
        val entry3 = Pair("Key3", "Entry3")

        Select(
            label = "hello",
            value = entry1,
            values = listOf(entry1, entry2, entry3),
            render = { it.second },
            onSelectionChanged = {
                /* do something with selected */
            },
        )
    }
}
