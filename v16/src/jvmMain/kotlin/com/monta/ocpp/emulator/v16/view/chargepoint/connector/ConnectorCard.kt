package com.monta.ocpp.emulator.v16.view.chargepoint.connector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
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
import androidx.compose.ui.unit.dp
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.common.components.Spinner
import com.monta.ocpp.emulator.common.components.toAmpString
import com.monta.ocpp.emulator.common.components.toKilowattString
import com.monta.ocpp.emulator.common.components.toReadable
import com.monta.ocpp.emulator.common.components.wattToKilowattString
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.data.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.v16.data.service.ChargePointConnectorService
import com.monta.ocpp.emulator.v16.data.util.idValue
import com.monta.ocpp.emulator.v16.service.ocpp.setMaxVehicleRate
import com.monta.ocpp.emulator.v16.service.ocpp.setNumberPhases
import com.monta.ocpp.emulator.v16.service.ocpp.stopActiveTransactions
import com.monta.ocpp.emulator.v16.view.chargepoint.authorizeComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun ConnectorCard(
    initConnector: ChargePointConnectorDAO
) {
    val coroutineScope = rememberCoroutineScope()
    val chargePointConnectorService: ChargePointConnectorService by injectAnywhere()

    var connector: ChargePointConnectorDAO by remember(initConnector.idValue) {
        mutableStateOf(initConnector)
    }

    var maxAmpsPerPhase by remember {
        mutableStateOf(
            connector.vehicleMaxAmpsPerPhase.toFloat()
        )
    }

    LaunchedEffect(connector.idValue) {
        coroutineScope.launch {
            chargePointConnectorService.getByIdFlow(
                coroutineScope = coroutineScope,
                id = initConnector.idValue
            ).collectLatest {
                connector = it
            }
        }
    }

    val activeTransaction = transaction {
        connector.activeTransaction
    }

    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "Connector ${connector.position}",
                style = MaterialTheme.typography.h5
            )
            authorizeComponent(connector)
        }
        Text("Status: ${connector.status}")
        Text("Status At: ${connector.statusAt.toReadable()}")
        Text("Meter: ${connector.meterWh.wattToKilowattString()} kWh")
        Text("Locked: ${connector.locked}")
        if (activeTransaction != null) {
            Text("Active Transaction: ${activeTransaction.externalId}")
        }
        Divider()
        Spinner(
            modifier = Modifier.fillMaxWidth(),
            label = "Vehicle number of phases",
            value = connector.vehicleNumberPhases,
            values = listOf(1, 2, 3),
            render = { it.toString() }
        ) { newValue ->
            launchThread {
                connector.setNumberPhases(newValue)
            }
        }
        Text(text = "Vehicle max amps per phase: ${connector.vehicleMaxAmpsPerPhase.toAmpString()}A")
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
                        amps = maxAmpsPerPhase.toDouble()
                    )
                }
            }
        )
        Divider()
        if (connector.activeTransactionId != null) {
            Text("Current charging speed: ${connector.kw.toKilowattString()}kW")
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    launchThread {
                        connector.stopActiveTransactions(
                            reason = Reason.Local,
                            endReasonDescription = "Stopped by user"
                        )
                    }
                }
            ) {
                Text("Stop transaction")
            }
        }
        VehicleStateView(connector)
        connectorStateView(connector)
    }
}
