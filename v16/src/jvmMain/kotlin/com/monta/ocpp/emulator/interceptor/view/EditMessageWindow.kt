package com.monta.ocpp.emulator.interceptor.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.theme.AppThemeViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Singleton
class EditMessageWindowViewModel {
    var channel by mutableStateOf<Channel<String>?>(null)
    var message by mutableStateOf("")
}

@Composable
fun ApplicationScope.EditMessageWindow() {
    val editMessageWindowViewModel: EditMessageWindowViewModel by injectAnywhere()

    val windowState = rememberWindowState(
        size = DpSize(800.dp, 750.dp),
        position = WindowPosition.Aligned(
            Alignment.CenterEnd,
        ),
    )

    if (editMessageWindowViewModel.channel == null) {
        return
    }

    val appThemeViewModel: AppThemeViewModel by injectAnywhere()

    Window(
        title = "Edit Message",
        state = windowState,
        onCloseRequest = {
            // TODO: proper way is probably not with runBlocking?
            runBlocking {
                editMessageWindowViewModel.channel?.send(editMessageWindowViewModel.message)
                editMessageWindowViewModel.message = ""
                editMessageWindowViewModel.channel = null
            }
        },
    ) {
        MaterialTheme(
            colors = appThemeViewModel.getColors(),
        ) {
            Scaffold(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Card(
                        modifier = getCardStyle().align(Alignment.TopCenter).fillMaxWidth().fillMaxHeight(),
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = editMessageWindowViewModel.message,
                                onValueChange = { newValue -> editMessageWindowViewModel.message = newValue },
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                                label = { Text("message payload") },
                            )
                            Button(
                                onClick = {
                                    runBlocking {
                                        editMessageWindowViewModel.channel?.send(editMessageWindowViewModel.message)
                                        editMessageWindowViewModel.message = ""
                                        editMessageWindowViewModel.channel = null
                                    }
                                },
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
        }
    }
}
