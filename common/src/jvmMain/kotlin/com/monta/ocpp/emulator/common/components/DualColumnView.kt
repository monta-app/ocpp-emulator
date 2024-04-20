package com.monta.ocpp.emulator.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DualColumView(
    firstColumn: @Composable () -> Unit,
    secondColumn: @Composable () -> Unit
) {
    Row {
        Column(
            modifier = Modifier.weight(1F)
                .fillMaxWidth()
                .verticalScroll(
                    state = rememberScrollState()
                )
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            firstColumn()
        }
        Column(
            modifier = Modifier.weight(1F)
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            secondColumn()
        }
    }
}
