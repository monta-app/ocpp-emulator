package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * [InputField] with form affordances: a warning icon + error message when
 * [isError], or a help tooltip when [helperText] is provided without an error.
 */
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
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        InputField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            label = label,
            enabled = enabled,
            isError = isError,
            trailingIcon = when {
                isError -> {
                    {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "error",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colors.error,
                        )
                    }
                }

                helperText != null -> {
                    {
                        MontaIcon(
                            iconName = "help",
                            contentDescription = "help",
                            tooltipText = helperText,
                        )
                    }
                }

                else -> null
            },
        )
        if (helperText != null && isError) {
            Text(
                text = helperText,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
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
