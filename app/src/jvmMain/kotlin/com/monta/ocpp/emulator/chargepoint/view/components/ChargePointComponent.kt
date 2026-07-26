package com.monta.ocpp.emulator.chargepoint.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.view.components.security.securityEventComponent
import com.monta.ocpp.emulator.common.components.Badge
import com.monta.ocpp.emulator.common.components.BadgeVariant
import com.monta.ocpp.emulator.common.components.CardDivider
import com.monta.ocpp.emulator.common.components.DetailRow
import com.monta.ocpp.emulator.common.components.SectionCard
import com.monta.ocpp.emulator.common.components.SectionLabel
import com.monta.ocpp.emulator.common.components.SegmentedToggle
import com.monta.ocpp.emulator.common.components.TextTooltip
import com.monta.ocpp.emulator.common.components.mutedForegroundColor
import com.monta.ocpp.emulator.common.components.svgPainterResource
import com.monta.ocpp.emulator.common.components.toReadable
import com.monta.ocpp.emulator.v16.setStatus
import kotlinx.coroutines.launch
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun chargePointComponent(
    chargePoint: ChargePointDAO,
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Header — identity on the left, live state + connect toggle on the right.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = svgPainterResource("icons/ev-charger.svg"),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.primary,
                    )
                    Text(
                        text = "Charge Point",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                TextTooltip(
                    text = "Click to copy identity",
                ) {
                    Text(
                        text = chargePoint.identity,
                        style = MaterialTheme.typography.body2,
                        color = mutedForegroundColor(),
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(StringSelection(chargePoint.identity)),
                                )
                            }
                        },
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(
                    status = chargePoint.status,
                )
                ChargePointConnectionButton(
                    chargePoint = chargePoint,
                )
            }
        }

        CardDivider()

        // Connection & health.
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DetailRow(
                label = "Connection",
            ) {
                Badge(
                    text = if (chargePoint.connected) "Connected" else "Disconnected",
                    variant = if (chargePoint.connected) BadgeVariant.Success else BadgeVariant.Neutral,
                )
            }
            DetailRow(
                label = "Latency",
                value = "${chargePoint.averageLatencyMillis} ms · ${chargePoint.messageCount} msgs",
            )
            DetailRow(
                label = "Status changed",
                value = chargePoint.statusAt.toReadable(),
            )
            DetailRow(
                label = "Firmware",
                value = chargePoint.firmware,
            )
            DetailRow(
                label = "Firmware status",
                value = "${chargePoint.firmwareStatus}",
            )
            DetailRow(
                label = "Diagnostics",
                value = "${chargePoint.diagnosticsStatus}",
            )
            if (chargePoint.errorCode != ChargePointErrorCode.NoError) {
                DetailRow(
                    label = "Error",
                ) {
                    Badge(
                        text = "${chargePoint.errorCode}",
                        variant = BadgeVariant.Destructive,
                    )
                }
            }
        }

        CardDivider()

        chargePointDisplayComponent(chargePoint)

        CardDivider()

        // Availability control.
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionLabel(
                text = "Availability",
            )
            SegmentedToggle(
                options = listOf(ChargePointStatus.Available, ChargePointStatus.Unavailable),
                selected = chargePoint.status,
                label = { "$it" },
                onSelect = { status ->
                    coroutineScope.launch {
                        chargePoint.setStatus(
                            status = status,
                        )
                    }
                },
            )
        }

        securityEventComponent(chargePoint)
    }
}
