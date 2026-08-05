package com.monta.ocpp.emulator.chargepoint.connector.ui

import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO

@Composable
fun ConnectorList(
    chargePoint: ChargePointDAO,
) {
    chargePoint.getConnectors()
        .sortedBy { it.position }
        .forEach { connector ->
            ConnectorCard(
                initConnector = connector,
            )
        }
}
