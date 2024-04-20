package com.monta.ocpp.emulator.v16.view.chargepoint

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.logger.ChargePointLogger
import com.monta.ocpp.emulator.theme.AppThemeViewModel
import com.monta.ocpp.emulator.v16.view.NavigationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun chargePointLogComponent(
    chargePointId: Long
) {
    val appThemeViewModel: AppThemeViewModel by injectAnywhere()

    val navigationViewModel: NavigationViewModel by injectAnywhere()

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    val logItems = remember {
        mutableStateListOf<ChargePointLogger.LogEntry>()
    }

    val logLevels = listOf(
        ChargePointLogger.Level.Info,
        ChargePointLogger.Level.Warn,
        ChargePointLogger.Level.Error
    )

    coroutineScope.launch {
        withContext(Dispatchers.IO) {
            ChargePointLogger.getLogger(chargePointId)
                .logFlow
                .collect { logEntry ->
                    if (logLevels.contains(logEntry.level)) {
                        logItems.add(logEntry)
                    }
                    coroutineScope.launch {
                        if (
                            !lazyListState.isScrollInProgress &&
                            logItems.size != 0 &&
                            navigationViewModel.windowHasFocus
                        ) {
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
                    modifier = getCardStyle()
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
                                    modifier = Modifier.fillMaxWidth(),
                                    color = appThemeViewModel.getLogColors(logItem)
                                )
                            }
                    }
                }
            }
        )
    }
}
