package com.monta.ocpp.emulator.interceptor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.monta.library.ocpp.common.profile.Feature
import com.monta.ocpp.emulator.designsystem.ui.component.PrimaryButton
import com.monta.ocpp.emulator.designsystem.ui.component.SegmentedToggle
import com.monta.ocpp.emulator.interceptor.model.Interception
import com.monta.ocpp.emulator.interceptor.model.InterceptionConfig
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor.Companion.centralSystemFeatures
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor.Companion.chargePointFeatures
import com.monta.ocpp.emulator.platform.util.injectAnywhere

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
                PrimaryButton(
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
        SegmentedToggle(
            options = InterceptionMode.entries,
            selected = InterceptionMode.of(state),
            label = { mode -> mode.name },
            onSelect = { mode ->
                onInterceptionChange(
                    when (mode) {
                        InterceptionMode.NoOp -> Interception.NoOp
                        InterceptionMode.Block -> Interception.Block(chargePointId)
                        InterceptionMode.Delay -> Interception.Delay(chargePointId, 20)
                        InterceptionMode.Edit -> Interception.Edit(25)
                    },
                )
            },
        )
    }
}

/** The four interception kinds as toggle options, ignoring their parameters. */
private enum class InterceptionMode {
    NoOp,
    Block,
    Delay,
    Edit,
    ;

    companion object {
        fun of(
            interception: Interception,
        ): InterceptionMode {
            return when (interception) {
                is Interception.NoOp -> NoOp
                is Interception.Block -> Block
                is Interception.Delay -> Delay
                is Interception.Edit -> Edit
                // Reject's intercept() is still a TODO, so the UI doesn't offer it.
                is Interception.Reject -> NoOp
            }
        }
    }
}
