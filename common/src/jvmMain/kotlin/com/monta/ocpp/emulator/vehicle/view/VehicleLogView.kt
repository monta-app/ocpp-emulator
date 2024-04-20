package com.monta.ocpp.emulator.vehicle.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.common.util.injectAnywhere
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun VehicleLogView() {
    val vehicleLogger: VehicleLogger by injectAnywhere()

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val logItems = mutableStateListOf<VehicleLogger.LogEntry>()

    val logLevels = listOf(
        VehicleLogger.Level.Info,
        VehicleLogger.Level.Warn,
        VehicleLogger.Level.Error
    )

    coroutineScope.launch {
        withContext(Dispatchers.IO) {
            vehicleLogger.logFlow
                .collect { logEntry ->
                    if (logLevels.contains(logEntry.level)) {
                        logItems.add(logEntry)
                    }
                    coroutineScope.launch {
                        if (logItems.size != 0) {
                            lazyListState.scrollToItem(logItems.size - 1)
                        }
                    }
                }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true,
        state = lazyListState

    ) {
        items(
            items = logItems,
            itemContent = { logItem ->
                Card(
                    modifier = getCardStyle(),
                    backgroundColor = when (logItem.level) {
                        VehicleLogger.Level.Error -> Color(204, 0, 0)
                        VehicleLogger.Level.Warn -> Color(196, 160, 0)
                        VehicleLogger.Level.Info -> Color.LightGray
                        VehicleLogger.Level.Debug -> Color(78, 154, 6)
                        VehicleLogger.Level.Trace -> Color(114, 159, 207)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        logItem.message.split("\r?\n|\r".toRegex())
                            .filter { it.isNotBlank() }
                            .forEach {
                                Text(
                                    text = it,
                                    softWrap = false,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                    }
                }
            }
        )
    }
}
