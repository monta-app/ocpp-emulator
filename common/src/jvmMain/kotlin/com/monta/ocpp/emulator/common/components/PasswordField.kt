package com.monta.ocpp.emulator.common.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.PasswordField(
    modifier: Modifier = Modifier,
    label: String = "Password",
    password: String,
    passwordVisibility: Boolean,
    passwordListener: (String) -> Unit,
    passwordVisibilityListener: (Boolean) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = { newValue ->
            passwordListener(newValue)
        },
        label = {
            Text(label)
        },
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    passwordVisibilityListener(!passwordVisibility)
                },
            ) {
                Icon(
                    painter = if (passwordVisibility) {
                        painterResource("icons/visibility_off.svg")
                    } else {
                        painterResource("icons/visibility.svg")
                    },
                    contentDescription = "",
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    )
}
