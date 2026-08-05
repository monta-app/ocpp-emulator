package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

/**
 * shadcn-style single-line text input: 36dp tall, hairline border with rounded
 * corners, transparent background, muted placeholder; the border switches to the
 * primary colour on focus (the "ring") and to the error colour on [isError]. A
 * quieter, denser alternative to the Material filled/outlined text fields.
 */
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        isError -> MaterialTheme.colors.error
        focused -> MaterialTheme.colors.primary
        else -> cardBorderColor()
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(36.dp)
            .alpha(if (enabled) 1f else 0.5f),
        enabled = enabled,
        singleLine = true,
        interactionSource = interactionSource,
        textStyle = MaterialTheme.typography.body2.copy(
            color = MaterialTheme.colors.onSurface,
        ),
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
    ) { innerTextField ->
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.body2,
                        color = mutedForegroundColor(),
                        maxLines = 1,
                    )
                }
                innerTextField()
            }
            trailingIcon?.invoke()
        }
    }
}
