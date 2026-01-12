package com.monta.ocpp.emulator.chargepointconnector.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.common.components.getCardStyle

@Composable
fun ConnectorList(
    chargePoint: ChargePointDAO,
) {
    chargePoint.getConnectors()
        .sortedBy { it.position }
        .forEach { connector ->
            Card(
                modifier = getCardStyle().fillMaxWidth(),
            ) {
                ConnectorCard(
                    initConnector = connector,
                )
            }
        }
}
