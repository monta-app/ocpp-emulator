package com.monta.ocpp.emulator.chargepoint.connector.ui

import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointDto

@Composable
fun ConnectorList(
    chargePoint: ChargePointDto,
) {
    chargePoint.connectors
        .forEach { connector ->
            ConnectorCard(
                initConnector = connector,
            )
        }
}
