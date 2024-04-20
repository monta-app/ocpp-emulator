package com.monta.ocpp.emulator.v16.view.chargepoint.connector

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.v16.data.entity.ChargePointDAO

@Composable
fun ConnectorList(
    chargePoint: ChargePointDAO
) {
    chargePoint.getConnectors()
        .sortedBy { it.position }
        .forEach { connector ->
            Card(
                modifier = getCardStyle().fillMaxWidth()
            ) {
                ConnectorCard(
                    initConnector = connector
                )
            }
        }
}
