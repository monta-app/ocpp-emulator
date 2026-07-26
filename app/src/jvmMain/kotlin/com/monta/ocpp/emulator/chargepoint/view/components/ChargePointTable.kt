package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.monta.ocpp.emulator.chargepoint.model.ChargePointMode
import com.monta.ocpp.emulator.common.components.AppDialog
import com.monta.ocpp.emulator.common.components.Badge
import com.monta.ocpp.emulator.common.components.BadgeVariant
import com.monta.ocpp.emulator.common.components.CardDivider
import com.monta.ocpp.emulator.common.components.DestructiveButton
import com.monta.ocpp.emulator.common.components.MontaIcon
import com.monta.ocpp.emulator.common.components.OutlineButton
import com.monta.ocpp.emulator.common.components.SectionCard
import com.monta.ocpp.emulator.common.components.mutedForegroundColor
import com.monta.ocpp.emulator.common.components.toKilowattString
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.model.UrlChoice
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.common.view.Navigator
import com.monta.ocpp.emulator.common.view.Screen
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
    SectionCard(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        TableHeader()
        CardDivider()
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
                CardDivider()
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
        color = mutedForegroundColor(),
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
            color = mutedForegroundColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Row(
            modifier = Modifier.width(ocppColumnWidth),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Badge(
                text = chargePoint.ocppVersion.versionNumber(),
                variant = BadgeVariant.Outline,
            )
        }
        Text(
            text = UrlChoice.fromUrl(chargePoint.ocppUrl).toString(),
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.7f),
        )
        Row(
            modifier = Modifier.weight(0.7f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Badge(
                text = chargePoint.operationMode.name,
                variant = when (chargePoint.operationMode) {
                    ChargePointMode.Auto -> BadgeVariant.Secondary
                    ChargePointMode.Manual -> BadgeVariant.Neutral
                },
            )
        }
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
            if (chargePoint.connected) {
                Badge(
                    text = "Connected",
                    iconName = "cloud",
                    variant = BadgeVariant.Success,
                )
            } else {
                Badge(
                    text = "Offline",
                    iconName = "cloud_off",
                    variant = BadgeVariant.Neutral,
                )
            }
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
    val navigator: Navigator by injectAnywhere()
    IconButton(
        onClick = {
            navigator.navigate(Screen.CreateChargePoint(chargePoint.idValue))
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

    AppDialog(
        onDismissRequest = {
            alertVisible = false
        },
        title = "Delete ${chargePoint.name.ifBlank { chargePoint.identity }}",
        description = "This permanently removes the charge point and its transactions. This can't be undone.",
        dismissButton = {
            OutlineButton(
                onClick = {
                    alertVisible = false
                },
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            DestructiveButton(
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
    )
}
