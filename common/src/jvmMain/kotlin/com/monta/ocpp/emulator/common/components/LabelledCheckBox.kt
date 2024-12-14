package com.monta.ocpp.emulator.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun LabelledCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                indication = ripple(color = MaterialTheme.colors.primary),
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onCheckedChange(!checked) }
            )
            .requiredHeight(ButtonDefaults.MinHeight)
            .padding(4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )

        Spacer(Modifier.size(6.dp))

        Text(
            text = label
        )
    }
}
