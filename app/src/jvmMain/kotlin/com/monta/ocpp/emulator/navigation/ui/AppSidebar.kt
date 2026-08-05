package com.monta.ocpp.emulator.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.designsystem.ui.component.MontaIcon
import com.monta.ocpp.emulator.designsystem.ui.component.TextTooltip
import com.monta.ocpp.emulator.designsystem.ui.component.mutedSurfaceColor
import com.monta.ocpp.emulator.designsystem.ui.component.svgPainterResource
import com.monta.ocpp.emulator.navigation.model.Screen
import com.monta.ocpp.emulator.navigation.model.TopLevelDestination
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import kotlinx.coroutines.flow.collectLatest

val SidebarWidth = 256.dp

/**
 * The app-wide navigation sidebar (shadcn dashboard style): a brand header followed by one menu
 * item per [TopLevelDestination]. Lives outside the `NavHost` in `MainWindow`, so it persists
 * across destinations; [selectedDestination] is derived from the back stack by the caller.
 */
@Composable
fun AppSidebar(
    selectedDestination: TopLevelDestination?,
) {
    val navigator: Navigator by injectAnywhere()
    val chargePointRepository: ChargePointRepository by injectAnywhere()
    val coroutineScope = rememberCoroutineScope()

    val chargePoints by produceState(initialValue = listOf<ChargePointDAO>()) {
        chargePointRepository.getAllFlow(coroutineScope)
            .collectLatest { newList ->
                value = newList
            }
    }
    val connectedIds = chargePoints.filter { it.connected }.map { it.idValue }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(SidebarWidth)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SidebarBrand()
        Spacer(
            modifier = Modifier.height(4.dp),
        )
        SidebarItem(
            label = "Charge Points",
            iconName = "ev-charger",
            selected = selectedDestination == TopLevelDestination.ChargePoints,
            onClick = {
                navigator.navigateTopLevel(Screen.ChargePoints)
            },
        )
        SidebarItem(
            label = "Connected",
            iconName = "globe-check",
            selected = selectedDestination == TopLevelDestination.Connected,
            enabled = connectedIds.isNotEmpty(),
            tooltip = if (connectedIds.isEmpty()) "No connected charge points" else null,
            onClick = {
                val lastActive = navigator.currentChargePointId
                val target = if (lastActive != null && lastActive in connectedIds) {
                    lastActive
                } else {
                    connectedIds.firstOrNull()
                }
                target?.let { chargePointId ->
                    navigator.navigateTopLevel(
                        Screen.ChargePoint(
                            chargePointId = chargePointId,
                        ),
                    )
                }
            },
        )
        SidebarItem(
            label = "Vehicles",
            iconName = "car",
            selected = selectedDestination == TopLevelDestination.Vehicles,
            onClick = {
                navigator.navigateTopLevel(Screen.Vehicles)
            },
        )
    }
}

@Composable
private fun SidebarBrand() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = svgPainterResource("logo.svg"),
            contentDescription = "Monta OCPP Emulator",
            // The logo is 149x28; pin the height and keep the aspect ratio.
            modifier = Modifier
                .height(20.dp)
                .aspectRatio(
                    ratio = 149f / 28f,
                    matchHeightConstraintsFirst = true,
                ),
            tint = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
private fun SidebarItem(
    label: String,
    iconName: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    tooltip: String? = null,
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colors.onBackground.copy(alpha = 0.38f)
        selected -> MaterialTheme.colors.onBackground
        else -> MaterialTheme.colors.onBackground.copy(alpha = 0.70f)
    }

    val item = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (selected) mutedSurfaceColor() else Color.Transparent)
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp,
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MontaIcon(
                iconName = iconName,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = contentColor,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.body2,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = contentColor,
            )
        }
    }

    if (tooltip != null) {
        TextTooltip(tooltip) {
            item()
        }
    } else {
        item()
    }
}
