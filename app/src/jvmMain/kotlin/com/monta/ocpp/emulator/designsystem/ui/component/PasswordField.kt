package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * [InputField] for secrets: masks the value and offers a visibility toggle as
 * the trailing icon.
 */
@Composable
fun ColumnScope.PasswordField(
    modifier: Modifier = Modifier,
    label: String = "Password",
    password: String,
    passwordVisibility: Boolean,
    passwordListener: (String) -> Unit,
    passwordVisibilityListener: (Boolean) -> Unit,
) {
    InputField(
        value = password,
        onValueChange = passwordListener,
        modifier = modifier,
        label = label,
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            Icon(
                painter = if (passwordVisibility) {
                    svgPainterResource("icons/visibility_off.svg")
                } else {
                    svgPainterResource("icons/visibility.svg")
                },
                contentDescription = if (passwordVisibility) "Hide password" else "Show password",
                modifier = Modifier
                    .size(16.dp)
                    .clickable {
                        passwordVisibilityListener(!passwordVisibility)
                    },
                tint = mutedForegroundColor(),
            )
        },
    )
}
