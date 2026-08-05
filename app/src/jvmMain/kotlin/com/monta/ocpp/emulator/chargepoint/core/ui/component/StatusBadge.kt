package com.monta.ocpp.emulator.chargepoint.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.designsystem.ui.component.Badge
import com.monta.ocpp.emulator.designsystem.ui.component.BadgeVariant

/**
 * Renders an OCPP [ChargePointStatus] as a coloured [Badge], shared by the
 * charge-point and connector cards so status reads the same everywhere.
 */
@Composable
fun StatusBadge(
    status: ChargePointStatus,
    modifier: Modifier = Modifier,
) {
    Badge(
        text = "$status",
        modifier = modifier,
        variant = status.badgeVariant(),
    )
}

/**
 * Maps an OCPP status onto a badge colour: green when ready, blue while
 * charging, amber for transitional states, red for faults, grey when offline.
 */
fun ChargePointStatus.badgeVariant(): BadgeVariant {
    return when (this) {
        ChargePointStatus.Available -> BadgeVariant.Success
        ChargePointStatus.Charging -> BadgeVariant.Default
        ChargePointStatus.Preparing -> BadgeVariant.Warning
        ChargePointStatus.SuspendedEV -> BadgeVariant.Warning
        ChargePointStatus.SuspendedEVSE -> BadgeVariant.Warning
        ChargePointStatus.Finishing -> BadgeVariant.Warning
        ChargePointStatus.Reserved -> BadgeVariant.Secondary
        ChargePointStatus.Unavailable -> BadgeVariant.Neutral
        ChargePointStatus.Faulted -> BadgeVariant.Destructive
    }
}
