package com.monta.ocpp.emulator.chargepoint.connector.ui

import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointSummary

@Composable
fun ConnectorList(
    chargePoint: ChargePointSummary,
) {
    chargePoint.connectors
        .forEach { connector ->
            ConnectorCard(
                initConnector = connector,
            )
        }
}
