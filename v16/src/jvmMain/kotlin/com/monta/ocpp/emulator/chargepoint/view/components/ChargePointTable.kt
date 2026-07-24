package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.common.components.MontaIcon
import com.monta.ocpp.emulator.common.components.MontaStateIcon
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.common.components.toKilowattString
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.model.UrlChoice
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.common.view.NavigationViewModel
import com.monta.ocpp.emulator.v16.connection.ConnectionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

private val ocppColumnWidth: Dp = 56.dp
private val maxKwColumnWidth: Dp = 64.dp
private val statusColumnWidth: Dp = 112.dp
private val actionsColumnWidth: Dp = 132.dp

/**
 * Data table listing all charge points. Clicking a row opens the charge
 * point; connect/edit/delete actions are available inline per row.
 */
@Composable
fun ChargePointTable(
    chargePoints: List<ChargePointDAO>,
    onRowClick: (ChargePointDAO) -> Unit,
) {
    Card(
        modifier = getCardStyle()
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column {
            TableHeader()
            Divider()
            LazyColumn {
                items(
                    items = chargePoints,
                    key = { chargePoint -> chargePoint.idValue },
                ) { chargePoint ->
                    TableRow(
                        chargePoint = chargePoint,
                        onClick = {
                            onRowClick(chargePoint)
                        },
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderCell(
            text = "Name",
            modifier = Modifier.weight(1.2f),
        )
        HeaderCell(
            text = "Identity",
            modifier = Modifier.weight(1f),
        )
        HeaderCell(
            text = "OCPP",
            modifier = Modifier.width(ocppColumnWidth),
        )
        HeaderCell(
            text = "Server",
            modifier = Modifier.weight(0.7f),
        )
        HeaderCell(
            text = "Mode",
            modifier = Modifier.weight(0.7f),
        )
        HeaderCell(
            text = "Max kW",
            modifier = Modifier.width(maxKwColumnWidth),
        )
        HeaderCell(
            text = "Status",
            modifier = Modifier.width(statusColumnWidth),
        )
        HeaderCell(
            text = "Actions",
            modifier = Modifier.width(actionsColumnWidth),
        )
    }
}

@Composable
private fun HeaderCell(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
private fun TableRow(
    chargePoint: ChargePointDAO,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = chargePoint.name.ifBlank { chargePoint.identity },
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            text = chargePoint.identity,
            style = MaterialTheme.typography.body2.copy(
                fontFamily = FontFamily.Monospace,
            ),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = chargePoint.ocppVersion.versionNumber(),
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            modifier = Modifier.width(ocppColumnWidth),
        )
        Text(
            text = UrlChoice.fromUrl(chargePoint.ocppUrl).toString(),
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.7f),
        )
        Text(
            text = chargePoint.operationMode.name,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.7f),
        )
        Text(
            text = chargePoint.maxKw.toKilowattString(),
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            modifier = Modifier.width(maxKwColumnWidth),
        )
        Row(
            modifier = Modifier.width(statusColumnWidth),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MontaStateIcon(
                state = chargePoint.connected,
                onState = "cloud",
                offState = "cloud_off",
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (chargePoint.connected) "Connected" else "Offline",
                style = MaterialTheme.typography.body2,
                color = if (chargePoint.connected) {
                    MaterialTheme.colors.onSurface
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                },
                maxLines = 1,
            )
        }
        Row(
            modifier = Modifier.width(actionsColumnWidth),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChargePointConnectionButton(chargePoint)
            ChargePointEditButton(chargePoint)
            ChargePointDeleteButton(chargePoint)
        }
    }
}

@Composable
private fun ChargePointEditButton(
    chargePoint: ChargePointDAO,
) {
    val navigationViewModel: NavigationViewModel by injectAnywhere()
    IconButton(
        onClick = {
            navigationViewModel.navigateTo(NavigationViewModel.Screen.CreateChargePoint(chargePoint))
        },
    ) {
        MontaIcon(
            iconName = "edit",
            contentDescription = "edit",
            tooltipText = "Edit charge point",
        )
    }
}

@Composable
private fun ChargePointDeleteButton(
    chargePoint: ChargePointDAO,
) {
    var alertVisible by remember {
        mutableStateOf(false)
    }

    IconButton(
        onClick = {
            alertVisible = true
        },
    ) {
        MontaIcon(
            iconName = "delete",
            contentDescription = "delete",
            tooltipText = "Delete charge point",
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
                },
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
                },
            ) {
                Text("Delete")
            }
        },
        title = {
            Text("Delete ${chargePoint.name.ifBlank { chargePoint.identity }}")
        },
        text = {
            Text("Are you sure you want to delete this charge point?")
        },
    )
}
