package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.common.components.MontaIcon
import com.monta.ocpp.emulator.common.components.MontaStateIcon
import com.monta.ocpp.emulator.common.components.TextTooltip
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.common.components.toKilowattString
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.model.UrlChoice
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.common.view.NavigationViewModel
import com.monta.ocpp.emulator.v16.connection.ConnectionManager
import org.jetbrains.exposed.sql.transactions.transaction

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChargePointCard(
    chargePoint: ChargePointDAO,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = getCardStyle().padding(8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chargePoint.name,
                    style = MaterialTheme.typography.h6
                )
                MontaStateIcon(
                    state = chargePoint.connected,
                    onState = "cloud",
                    offState = "cloud_off"
                )
            }
            Divider(modifier = Modifier.padding(top = 8.dp))
            TextWithLabel(
                label = "Identity",
                value = chargePoint.identity
            )
            TextWithLabel(
                label = "OCPP version",
                value = chargePoint.ocppVersion.versionNumber()
            )
            TextWithLabel(
                label = "Server",
                value = UrlChoice.fromUrl(chargePoint.ocppUrl).toString()
            )
            TextWithLabel(
                label = "Mode",
                value = chargePoint.operationMode.name
            )
            TextWithLabel(
                label = "Max kW",
                value = chargePoint.maxKw.toKilowattString()
            )
            Divider(modifier = Modifier.padding(top = 16.dp))
            Row {
                ChargePointConnectionButton(chargePoint)
                ChargePointEditButton(chargePoint)
                ChargePointDeleteButton(chargePoint)
            }
        }
    }
}

@Composable
fun TextWithLabel(
    label: String,
    value: String?
) {
    Row(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().weight(1f),
            text = "$label: ",
            style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                letterSpacing = 0.15.sp
            )
        )
        Text(
            modifier = Modifier.fillMaxWidth().weight(1f),
            text = value ?: "",
            style = MaterialTheme.typography.body2
        )
    }
}

/**
 * Displays the state of a charge point,
 * and allows the user to connect or disconnect the charge point quickly
 */
@Composable
private fun ChargePointConnectionButton(
    chargePoint: ChargePointDAO
) {
    val connectionManager: ConnectionManager by injectAnywhere()

    TextTooltip(
        text = if (chargePoint.connected) {
            "Disconnect charge point"
        } else {
            "Connect charge point"
        }
    ) {
        IconButton(
            onClick = {
                if (chargePoint.connected) {
                    connectionManager.disconnect(chargePoint.idValue)
                } else {
                    connectionManager.connect(chargePoint.idValue)
                }
            }
        ) {
            MontaStateIcon(
                state = chargePoint.connected,
                onState = "stop_circle",
                offState = "play_circle"
            )
        }
    }
}

@Composable
private fun ChargePointEditButton(
    chargePoint: ChargePointDAO
) {
    val navigationViewModel: NavigationViewModel by injectAnywhere()
    IconButton(
        onClick = {
            navigationViewModel.navigateTo(NavigationViewModel.Screen.CreateChargePoint(chargePoint))
        }
    ) {
        MontaIcon(
            iconName = "edit",
            contentDescription = "edit",
            tooltipText = "Edit charge point"
        )
    }
}

@Composable
private fun ChargePointDeleteButton(
    chargePoint: ChargePointDAO
) {
    var alertVisible by remember {
        mutableStateOf(false)
    }

    IconButton(
        onClick = {
            alertVisible = true
        }
    ) {
        MontaIcon(
            iconName = "delete",
            contentDescription = "delete",
            tooltipText = "Delete charge point"
        )
    }

    if (!alertVisible) {
        return
    }

    AlertDialog(
        onDismissRequest = {
            alertVisible = false
        },
        dismissButton = {
            Button(
                onClick = {
                    alertVisible = false
                }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    launchThread {
                        val connectionManager: ConnectionManager by injectAnywhere()
                        connectionManager.disconnect(chargePoint.idValue)
                        transaction {
                            chargePoint.delete()
                            chargePoint.connectors.forEach { connector ->
                                connector.transactions.forEach { transaction ->
                                    transaction.delete()
                                }
                                connector.delete()
                            }
                        }
                        alertVisible = false
                    }
                }
            ) {
                Text("Delete")
            }
        },
        title = {
            Text("Delete ${chargePoint.name}")
        },
        text = {
            Text("Are you sure you want to delete this charge point?")
        }
    )
}
