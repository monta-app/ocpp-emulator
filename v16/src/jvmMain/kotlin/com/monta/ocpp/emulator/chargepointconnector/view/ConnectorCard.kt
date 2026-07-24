package com.monta.ocpp.emulator.chargepointconnector.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.view.components.StatusBadge
import com.monta.ocpp.emulator.chargepoint.view.components.authorizeComponent
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.common.components.CardDivider
import com.monta.ocpp.emulator.common.components.DetailRow
import com.monta.ocpp.emulator.common.components.OutlineButton
import com.monta.ocpp.emulator.common.components.SectionCard
import com.monta.ocpp.emulator.common.components.SectionLabel
import com.monta.ocpp.emulator.common.components.Spinner
import com.monta.ocpp.emulator.common.components.mutedForegroundColor
import com.monta.ocpp.emulator.common.components.toAmpString
import com.monta.ocpp.emulator.common.components.toKilowattString
import com.monta.ocpp.emulator.common.components.toReadable
import com.monta.ocpp.emulator.common.components.wattToKilowattString
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.setMaxVehicleRate
import com.monta.ocpp.emulator.v16.setNumberPhases
import com.monta.ocpp.emulator.v16.stopActiveTransactions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun ConnectorCard(
    initConnector: ChargePointConnectorDAO,
) {
    val coroutineScope = rememberCoroutineScope()
    val chargePointConnectorService: ChargePointConnectorService by injectAnywhere()

    var connector: ChargePointConnectorDAO by remember(initConnector.idValue) {
        mutableStateOf(initConnector)
    }

    var maxAmpsPerPhase by remember {
        mutableStateOf(
            connector.vehicleMaxAmpsPerPhase.toFloat(),
        )
    }

    LaunchedEffect(connector.idValue) {
        coroutineScope.launch {
            chargePointConnectorService.getByIdFlow(
                coroutineScope = coroutineScope,
                id = initConnector.idValue,
            ).collectLatest {
                connector = it
            }
        }
    }

    val activeTransaction = transaction {
        connector.activeTransaction
    }

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Header — connector number on the left, RFID authorize action on the right.
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "Connector ${connector.position}",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold,
            )
            authorizeComponent(connector)
        }

        CardDivider()

        // Live state.
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DetailRow(
                label = "Status",
            ) {
                StatusBadge(
                    status = connector.status,
                )
            }
            DetailRow(
                label = "Status changed",
                value = connector.statusAt.toReadable(),
            )
            DetailRow(
                label = "Meter",
                value = "${connector.meterWh.wattToKilowattString()} kWh",
            )
            DetailRow(
                label = "Locked",
                value = if (connector.locked) "Yes" else "No",
            )
            if (activeTransaction != null) {
                DetailRow(
                    label = "Active transaction",
                    value = "${activeTransaction.externalId}",
                )
            }
        }

        CardDivider()

        // Vehicle charging parameters.
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spinner(
                modifier = Modifier.fillMaxWidth(),
                label = "Vehicle number of phases",
                value = connector.vehicleNumberPhases,
                values = listOf(1, 2, 3),
                render = { it.toString() },
            ) { newValue ->
                launchThread {
                    connector.setNumberPhases(newValue)
                }
            }
            Text(
                text = "Vehicle max amps per phase: ${connector.vehicleMaxAmpsPerPhase.toAmpString()} A",
                style = MaterialTheme.typography.body2,
                color = mutedForegroundColor(),
            )
            val maxValueRange = ceil(connector.maxKw * 1000 / 230).toFloat()
            Slider(
                value = maxAmpsPerPhase,
                valueRange = 0F..maxValueRange,
                onValueChange = { value ->
                    maxAmpsPerPhase = (value * 10).roundToInt() / 10F
                },
                onValueChangeFinished = {
                    launchThread {
                        connector.setMaxVehicleRate(
                            amps = maxAmpsPerPhase.toDouble(),
                        )
                    }
                },
            )
        }

        if (connector.activeTransactionId != null) {
            CardDivider()
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DetailRow(
                    label = "Charging speed",
                    value = "${connector.kw.toKilowattString()} kW",
                )
                OutlineButton(
                    onClick = {
                        launchThread {
                            connector.stopActiveTransactions(
                                reason = Reason.Local,
                                endReasonDescription = "Stopped by user",
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Stop transaction")
                }
            }
        }

        CardDivider()

        // Simulator controls.
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionLabel(
                text = "Vehicle state",
            )
            VehicleStateView(connector)
            ConnectorStateView(connector)
        }
    }
}
