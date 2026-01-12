package com.monta.ocpp.emulator.interceptor.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.monta.library.ocpp.common.profile.Feature
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.interceptor.Interception
import com.monta.ocpp.emulator.interceptor.InterceptionConfig
import com.monta.ocpp.emulator.interceptor.MessageInterceptor
import com.monta.ocpp.emulator.interceptor.MessageInterceptor.Companion.centralSystemFeatures
import com.monta.ocpp.emulator.interceptor.MessageInterceptor.Companion.chargePointFeatures

@Preview
@Composable
fun InterceptorConfigComponent(
    chargePointId: Long,
) {
    val messageInterceptor: MessageInterceptor by injectAnywhere()
    Column(
        modifier = Modifier
            .width(320.dp)
            .padding(8.dp)
            .verticalScroll(
                state = rememberScrollState(),
            ),
    ) {
        Row {
            Text("Charge Point Initiated", fontWeight = W700)
        }
        chargePointFeatures
            .forEach {
                InterceptionConfigRow(
                    chargePointId = chargePointId,
                    messageType = it.value,
                    config = messageInterceptor.messageTypeConfig[chargePointId]?.get(it.key)!!,
                    sendIt = true,
                )
            }
        Row {
            Text("Central System Initiated", fontWeight = W700)
        }
        centralSystemFeatures
            .forEach {
                InterceptionConfigRow(
                    chargePointId = chargePointId,
                    messageType = it.value,
                    config = messageInterceptor.messageTypeConfig[chargePointId]?.get(it.key)!!,
                    sendIt = false,
                )
            }
    }
}

@Composable
private fun InterceptionConfigRow(
    chargePointId: Long,
    messageType: Feature,
    config: InterceptionConfig,
    sendIt: Boolean,
) {
    Column {
        Text(
            messageType.name,
        )
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MessageTypeInterceptionConfig(
                        chargePointId = chargePointId,
                        title = "Request",
                        state = config.onRequest.value,
                        onInterceptionChange = {
                            config.onRequest.value = it
                        },
                    )
                    MessageTypeInterceptionConfig(
                        chargePointId = chargePointId,
                        title = "Response",
                        state = config.onResponse.value,
                        onInterceptionChange = {
                            config.onResponse.value = it
                        },
                    )
                }
            }
            if (sendIt) {
                Button(
                    onClick = {
                        val sendMessageWindowViewModel: SendMessageWindowViewModel by injectAnywhere()
                        sendMessageWindowViewModel.messageType = messageType
                        sendMessageWindowViewModel.messageYaml = defaultPayload(messageType)
                    },
                ) {
                    Text("Send it")
                }
            }
        }
    }
}

@Composable
fun MessageTypeInterceptionConfig(
    chargePointId: Long,
    title: String,
    state: Interception,
    onInterceptionChange: (Interception) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(title, fontSize = 0.7.em)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedButton(
                colors = buttonColors(state is Interception.NoOp),
                onClick = {
                    onInterceptionChange(Interception.NoOp)
                },
            ) {
                Text("NoOp")
            }

            OutlinedButton(
                colors = buttonColors(state is Interception.Block),
                onClick = {
                    onInterceptionChange(Interception.Block(chargePointId))
                },
            ) {
                Text("Block")
            }

            OutlinedButton(
                colors = buttonColors(state is Interception.Delay),
                onClick = {
                    onInterceptionChange(Interception.Delay(chargePointId, 20))
                },
            ) {
                Text("Delay")
            }

            OutlinedButton(
                colors = buttonColors(state is Interception.Edit),
                onClick = {
                    onInterceptionChange(Interception.Edit(25))
                },
            ) {
                Text("Edit")
            }
        }
    }
}

@Composable
private fun buttonColors(
    selected: Boolean,
) = ButtonDefaults.outlinedButtonColors(
    backgroundColor = if (selected) {
        MaterialTheme.colors.primary.copy(
            alpha = .8f,
        )
    } else {
        MaterialTheme.colors.onPrimary
    },
    contentColor = if (selected) {
        MaterialTheme.colors.onPrimary
    } else {
        MaterialTheme.colors.primary
    },
)
