package com.monta.ocpp.emulator.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

@Composable
fun BackButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}

@Composable
fun RowScope.InterceptionToggle(
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    Spacer(modifier = Modifier.weight(1f))
    IconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange
    ) {
        Text("\uD83E\uDD13", fontSize = 2.em)
    }
}

@Composable
fun RfidButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            painter = painterResource("icons/rfid.svg"),
            contentDescription = "Authorize",
            modifier = Modifier
                .padding(0.dp)
                .size(16.dp)
        )
    }
}
