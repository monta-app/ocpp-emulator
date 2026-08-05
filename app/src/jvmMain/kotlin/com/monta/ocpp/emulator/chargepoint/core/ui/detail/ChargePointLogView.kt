package com.monta.ocpp.emulator.chargepoint.core.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monta.ocpp.emulator.designsystem.ui.component.SectionCard
import com.monta.ocpp.emulator.designsystem.ui.component.SectionLabel
import com.monta.ocpp.emulator.designsystem.ui.component.mutedForegroundColor
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.platform.logging.service.ChargePointLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IndexOutOfBoundsException

// A terminal reads as a terminal in any app theme, so the surface uses a fixed
// dark palette rather than the Material one. Levels borrow familiar terminal
// hues, kept bright enough to stay legible on the near-black background.
private val TerminalBackground = Color(0xFF0D1117)
private val TerminalBorder = Color(0xFF30363D)
private val TerminalPrompt = Color(0xFF3FB950)
private val TerminalError = Color(0xFFFF6B6B)
private val TerminalWarn = Color(0xFFE3B341)
private val TerminalInfo = Color(0xFFE6EDF3)

private fun terminalColor(
    level: ChargePointLogger.Level,
): Color {
    return when (level) {
        ChargePointLogger.Level.Error -> TerminalError
        ChargePointLogger.Level.Warn -> TerminalWarn
        ChargePointLogger.Level.Info -> TerminalInfo
        ChargePointLogger.Level.Debug -> Color(0xFF3FB950)
        ChargePointLogger.Level.Trace -> Color(0xFF79C0FF)
    }
}

@Composable
fun chargePointLogComponent(
    chargePointId: Long,
    modifier: Modifier = Modifier,
) {
    val navigator: Navigator by injectAnywhere()

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    val logItems = remember {
        mutableStateListOf<ChargePointLogger.LogEntry>()
    }

    val logLevels = listOf(
        ChargePointLogger.Level.Info,
        ChargePointLogger.Level.Warn,
        ChargePointLogger.Level.Error,
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
                            navigator.windowHasFocus
                        ) {
                            try {
                                lazyListState.scrollToItem(logItems.size - 1)
                            } catch (ignore: IndexOutOfBoundsException) { /* ignore - this happens sometimes */ }
                        }
                    }
                }
        }
    }

    SectionCard(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(8.dp)
                        .clip(CircleShape)
                        .background(TerminalPrompt),
                )
                SectionLabel(
                    text = "Logs",
                )
            }
            Text(
                text = "Info · Warn · Error",
                style = MaterialTheme.typography.caption,
                color = mutedForegroundColor(),
            )
        }
        Surface(
            modifier = Modifier.fillMaxWidth()
                .weight(1F),
            shape = RoundedCornerShape(8.dp),
            color = TerminalBackground,
            border = BorderStroke(1.dp, TerminalBorder),
            elevation = 0.dp,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                reverseLayout = true,
                state = lazyListState,
            ) {
                items(
                    items = logItems,
                    itemContent = { logItem ->
                        Column {
                            logItem.message.split("\r?\n|\r".toRegex())
                                .filter { it.isNotBlank() }
                                .forEach {
                                    Text(
                                        text = it,
                                        softWrap = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = terminalColor(logItem.level),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                        }
                    },
                )
            }
        }
    }
}
