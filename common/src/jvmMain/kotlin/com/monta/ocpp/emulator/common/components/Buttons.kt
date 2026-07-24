package com.monta.ocpp.emulator.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// shadcn buttons are flat (no elevation) with a small corner radius; these two
// wrappers bake that in so call sites stay terse and consistent.
private val ButtonShape = RoundedCornerShape(8.dp)

@Composable
private fun flatElevation() = ButtonDefaults.elevation(
    defaultElevation = 0.dp,
    pressedElevation = 0.dp,
    disabledElevation = 0.dp,
    hoveredElevation = 0.dp,
    focusedElevation = 0.dp,
)

/**
 * Filled accent button — shadcn's `default` variant. Flat, primary-coloured.
 */
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonShape,
        elevation = flatElevation(),
        content = content,
    )
}

/**
 * Bordered, transparent button with foreground text — shadcn's `outline`
 * variant. The quiet default for secondary actions.
 */
@Composable
fun OutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonShape,
        border = BorderStroke(1.dp, cardBorderColor()),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colors.onSurface,
        ),
        content = content,
    )
}

/**
 * Filled danger button — shadcn's `destructive` variant. For irreversible
 * actions such as delete.
 */
@Composable
fun DestructiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonShape,
        elevation = flatElevation(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.error,
            contentColor = MaterialTheme.colors.onError,
        ),
        content = content,
    )
}

/**
 * A row of mutually-exclusive options where the active one reads as a filled
 * [PrimaryButton] and the rest as quiet [OutlineButton]s — shadcn's segmented
 * toggle. Options share the width evenly.
 */
@Composable
fun <T> SegmentedToggle(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    onSelect: (T) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            if (option == selected) {
                PrimaryButton(
                    onClick = { onSelect(option) },
                    modifier = Modifier.weight(1F),
                ) {
                    Text(label(option))
                }
            } else {
                OutlineButton(
                    onClick = { onSelect(option) },
                    modifier = Modifier.weight(1F),
                ) {
                    Text(label(option))
                }
            }
        }
    }
}
