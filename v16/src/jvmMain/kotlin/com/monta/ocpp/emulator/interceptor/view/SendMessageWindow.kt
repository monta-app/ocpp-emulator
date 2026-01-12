package com.monta.ocpp.emulator.interceptor.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.serialization.Message
import com.monta.library.ocpp.common.serialization.MessageSerializer
import com.monta.library.ocpp.common.serialization.SerializationMode
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.Context
import com.monta.library.ocpp.v16.SampledValue
import com.monta.library.ocpp.v16.ValueFormat
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.core.AuthorizeFeature
import com.monta.library.ocpp.v16.core.AuthorizeRequest
import com.monta.library.ocpp.v16.core.BootNotificationFeature
import com.monta.library.ocpp.v16.core.BootNotificationRequest
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.DataTransferFeature
import com.monta.library.ocpp.v16.core.DataTransferRequest
import com.monta.library.ocpp.v16.core.HeartbeatFeature
import com.monta.library.ocpp.v16.core.HeartbeatRequest
import com.monta.library.ocpp.v16.core.MeterValue
import com.monta.library.ocpp.v16.core.MeterValuesFeature
import com.monta.library.ocpp.v16.core.MeterValuesRequest
import com.monta.library.ocpp.v16.core.StartTransactionFeature
import com.monta.library.ocpp.v16.core.StartTransactionRequest
import com.monta.library.ocpp.v16.core.StatusNotificationFeature
import com.monta.library.ocpp.v16.core.StatusNotificationRequest
import com.monta.library.ocpp.v16.core.StopTransactionFeature
import com.monta.library.ocpp.v16.core.StopTransactionRequest
import com.monta.library.ocpp.v16.error.OcppErrorResponderV16
import com.monta.library.ocpp.v16.firmware.DiagnosticsStatusNotificationFeature
import com.monta.library.ocpp.v16.firmware.DiagnosticsStatusNotificationRequest
import com.monta.library.ocpp.v16.firmware.DiagnosticsStatusNotificationStatus
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationFeature
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationRequest
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationStatus
import com.monta.ocpp.emulator.chargepoint.entity.PreviousMessagesDAO
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.service.PreviousMessagesService
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.PrettyYamlFormatter
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.view.NavigationViewModel
import com.monta.ocpp.emulator.logger.ChargePointLogger
import com.monta.ocpp.emulator.theme.AppThemeViewModel
import com.monta.ocpp.emulator.v16.util.MeterValuesGenerator
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Singleton

@Singleton
class SendMessageWindowViewModel {
    var messageType by mutableStateOf<Feature?>(null)
    var messageYaml by mutableStateOf("")
    var previousMessages = mutableStateOf<List<PreviousMessagesDAO>>(listOf())
}

@Composable
fun ApplicationScope.SendMessageWindow() {
    val sendMessageWindowViewModel: SendMessageWindowViewModel by injectAnywhere()

    val windowState = rememberWindowState(
        size = DpSize(800.dp, 750.dp),
        position = WindowPosition.Aligned(
            Alignment.CenterEnd,
        ),
    )

    if (sendMessageWindowViewModel.messageType == null) {
        return
    }

    val appThemeViewModel: AppThemeViewModel by injectAnywhere()
    val navigationViewModel: NavigationViewModel by injectAnywhere()
    val chargePointService: ChargePointService by injectAnywhere()
    val previousMessagesService: PreviousMessagesService by injectAnywhere()

    val chargePoint = chargePointService.getById(
        navigationViewModel.getChargePointId(),
    )
    sendMessageWindowViewModel.previousMessages.value = previousMessagesService.getAllOfMessageType(
        sendMessageWindowViewModel.messageType?.name ?: "",
    )

    Window(
        title = "Send Message",
        state = windowState,
        onCloseRequest = {
            sendMessageWindowViewModel.messageType = null
        },
    ) {
        MaterialTheme(
            colors = appThemeViewModel.getColors(),
        ) {
            Scaffold(
                modifier = Modifier.fillMaxWidth(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Send message: ${sendMessageWindowViewModel.messageType?.name}")
                        },
                    )
                },
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
                                value = sendMessageWindowViewModel.messageYaml,
                                onValueChange = { newValue -> sendMessageWindowViewModel.messageYaml = newValue },
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                                label = { Text("message payload") },
                            )
                            Button(
                                onClick = {
                                    runBlocking {
                                        previousMessagesService.insertNewMessage(
                                            messageType = sendMessageWindowViewModel.messageType?.name ?: "",
                                            message = sendMessageWindowViewModel.messageYaml,
                                        )

                                        val payload = PrettyYamlFormatter.readYaml(
                                            sendMessageWindowViewModel.messageYaml,
                                            sendMessageWindowViewModel.messageType!!.requestType,
                                        )
                                        val ocppClientV16: OcppClientV16 by injectAnywhere()
                                        ocppClientV16.sendMessage(
                                            OcppSession.Info(
                                                serverId = "",
                                                identity = chargePoint.identity,
                                            ),
                                            Message.Request(
                                                uniqueId = UUID.randomUUID().toString(),
                                                action = sendMessageWindowViewModel.messageType!!.name,
                                                payload = MessageSerializer(
                                                    SerializationMode.OCPP_1_6,
                                                    OcppErrorResponderV16,
                                                ).toPayload(
                                                    value = payload,
                                                ),
                                            ),
                                        )
                                        ChargePointLogger.getLogger(navigationViewModel.getChargePointId()).info(
                                            0,
                                            "Sent message: ${sendMessageWindowViewModel.messageType!!.name}",
                                        )
                                        sendMessageWindowViewModel.messageType = null
                                    }
                                },
                            ) {
                                Text("Send")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Previous Messages", fontSize = 1.2.em)
                            sendMessageWindowViewModel.previousMessages.value.forEach { previousMessage ->
                                Card(
                                    modifier = getCardStyle()
                                        .fillMaxWidth()
                                        .fillMaxHeight().padding(2.dp),
                                    border = BorderStroke(width = Dp.Hairline, color = Color.Gray),
                                ) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        ClickableText(
                                            text = AnnotatedString(previousMessage.message),
                                            style = TextStyle(
                                                color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.surface),
                                                fontFamily = FontFamily.Monospace,
                                            ),
                                            modifier = Modifier.padding(12.dp).pointerHoverIcon(PointerIcon.Hand),
                                            onClick = {
                                                sendMessageWindowViewModel.messageYaml = previousMessage.message
                                            },
                                        )
                                        Button(
                                            modifier = Modifier.padding(12.dp).pointerHoverIcon(PointerIcon.Hand),
                                            onClick = {
                                                previousMessagesService.deleteMessage(previousMessage.id.value)
                                                sendMessageWindowViewModel.previousMessages.value =
                                                    sendMessageWindowViewModel.previousMessages.value
                                                        .filter { it.idValue != previousMessage.idValue }
                                            },
                                        ) {
                                            Text("Delete")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun defaultPayload(
    messageType: Feature,
): String {
    val navigationViewModel: NavigationViewModel by injectAnywhere()
    val chargePointService: ChargePointService by injectAnywhere()

    val chargePoint = chargePointService.getById(
        navigationViewModel.getChargePointId(),
    )

    val transaction = transaction {
        chargePoint.getActiveTransactions().firstOrNull()
    }

    val request = when (messageType) {
        AuthorizeFeature -> AuthorizeRequest("")
        BootNotificationFeature -> BootNotificationRequest(
            chargePointSerialNumber = chargePoint.serial,
            firmwareVersion = chargePoint.firmware,
            chargePointModel = chargePoint.model,
            chargePointVendor = chargePoint.brand,
        )

        DataTransferFeature -> DataTransferRequest(
            vendorId = "generalConfiguration",
            messageId = "setMeterConfiguration",
            data = "{}",
        )

        DiagnosticsStatusNotificationFeature -> DiagnosticsStatusNotificationRequest(DiagnosticsStatusNotificationStatus.Idle)
        FirmwareStatusNotificationFeature -> FirmwareStatusNotificationRequest(FirmwareStatusNotificationStatus.Idle)
        HeartbeatFeature -> HeartbeatRequest
        MeterValuesFeature -> MeterValuesRequest(
            connectorId = transaction?.connectorPosition ?: 1,
            transactionId = transaction?.externalId,
            meterValue = listOf(
                MeterValue(
                    timestamp = ZonedDateTime.now(),
                    sampledValue = MeterValuesGenerator.generate(
                        meterValuesSampledData = chargePoint.configuration.meterValuesSampledData,
                        startTime = transaction?.startTime,
                        endMeter = transaction?.endMeter ?: 0.0,
                        watts = chargePoint.getConnector(transaction?.connectorPosition ?: 1).kw * 1000,
                    ),
                ),
            ),
        )

        StartTransactionFeature -> StartTransactionRequest(
            connectorId = 1,
            idTag = "",
            meterStart = 0,
            timestamp = ZonedDateTime.now(),
        )

        StatusNotificationFeature -> StatusNotificationRequest(
            connectorId = 1,
            errorCode = ChargePointErrorCode.NoError,
            status = ChargePointStatus.Available,
        )

        StopTransactionFeature -> StopTransactionRequest(
            idTag = transaction?.idTag,
            meterStop = chargePoint.getConnector(transaction?.connectorPosition ?: 1).meterWh.toInt(),
            timestamp = ZonedDateTime.now(),
            transactionId = transaction?.id?.value?.toInt() ?: 0,
            transactionData = listOf(
                MeterValue(
                    timestamp = transaction?.startTime?.atZone(ZoneOffset.UTC),
                    sampledValue = listOf(
                        SampledValue(
                            value = "OCMF|{\"FV\":\"1.0\",\"GI\":\"7cc7af6f-5c10-4f2f-aa95-b3570606b564\",\"GS\":\"\",\"GV\":\"1.1.2\",\"PG\":\"T74\",\"MV\":\"Gossen Metrawatt\",\"MM\":\"EM2289\",\"MS\":\"FI7309540155\",\"MF\":\"03.03\",\"IS\":true,\"IT\":\"ISO14443\",\"ID\":\"04340fca3c6f80\",\"RD\":[{\"TM\":\"2023-01-20T22:12:40,000+0100 I\",\"TX\":\"B\",\"RV\":166841,\"RI\":\"1-b:1.8.0\",\"RU\":\"Wh\",\"RT\":\"AC\",\"EF\":\"\",\"ST\":\"G\"}]}|{\"SD\":\"304402201B26AB8E9EA9A55CEAADE226713B92AD2DAF6189FC36472DF4E82EB577BC1D09022034521C01EF03E7A1A2F48820357AE7E77BD87975AF393FC4362419B79321D876\"}",
                            context = Context.TransactionBegin.name,
                            format = ValueFormat.SignedData.name,
                            measurand = "Energy.Active.Import.Register",
                            unit = "Wh",
                        ),
                    ),
                ),
                MeterValue(
                    timestamp = ZonedDateTime.now(),
                    sampledValue = listOf(
                        SampledValue(
                            value = "OCMF|{\"FV\":\"1.0\",\"GI\":\"7cc7af6f-5c10-4f2f-aa95-b3570606b564\",\"GS\":\"\",\"GV\":\"1.1.2\",\"PG\":\"T75\",\"MV\":\"Gossen Metrawatt\",\"MM\":\"EM2289\",\"MS\":\"FI7309540155\",\"MF\":\"03.03\",\"IS\":true,\"IT\":\"ISO14443\",\"ID\":\"04340fca3c6f80\",\"RD\":[{\"TM\":\"2023-01-21T11:34:04,000+0100 R\",\"TX\":\"E\",\"RV\":174728,\"RI\":\"1-b:1.8.0\",\"RU\":\"Wh\",\"RT\":\"AC\",\"EF\":\"\",\"ST\":\"G\"}]}|{\"SD\":\"3046022100B352936C33766E4A7F07076E5A6C767A176584809F5BD6C6F7F17D933BE65BEC022100B469E02A452937F63962C66A05C6A7AB6D240AEACC02C83667F18AE8E79E239B\"}",
                            context = Context.TransactionEnd.name,
                            format = ValueFormat.SignedData.name,
                            measurand = "Energy.Active.Import.Register",
                            unit = "Wh",
                        ),
                    ),
                ),
            ),
        )

        else -> throw NotImplementedError("Unknown message type ${messageType.name}")
    }
    return PrettyYamlFormatter.writeYaml(request)
}
