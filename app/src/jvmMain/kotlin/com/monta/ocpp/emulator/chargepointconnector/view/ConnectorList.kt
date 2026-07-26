package com.monta.ocpp.emulator.chargepointconnector.view

import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO

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
