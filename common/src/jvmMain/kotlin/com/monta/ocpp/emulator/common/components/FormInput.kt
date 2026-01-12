package com.monta.ocpp.emulator.common.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormInput(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    helperText: String? = null,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
            },
            modifier = modifier,
            label = {
                Text(label)
            },
            isError = isError,
            enabled = enabled,
            maxLines = 1,
            trailingIcon = {
                if (isError) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "error",
                        tint = MaterialTheme.colors.error,
                    )
                } else if (helperText != null) {
                    MontaIcon(
                        iconName = "help",
                        contentDescription = "help",
                        tooltipText = helperText,
                    )
                }
            },
        )
        if (helperText != null && isError) {
            Text(
                text = helperText,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(
                    top = 4.dp,
                    start = 8.dp,
                ),
            )
        }
    }
}

@Composable
@Preview
private fun FormInput_Preview() {
    Box(
        modifier = Modifier.padding(16.dp),
    ) {
        FormInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Charge Point Name",
            value = "",
            onValueChange = { },
            isError = true,
            helperText = "Charge point identifier already used",
        )
    }
}
