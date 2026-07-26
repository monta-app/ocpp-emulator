package com.monta.ocpp.emulator.common.view

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations, consumed by the [androidx.navigation.compose.NavHost] in
 * `MainWindow`. Each destination is [Serializable] so Navigation Compose can encode it into the
 * back stack.
 *
 * Note: routes carry only serializable primitives. Screens that need a `ChargePointDAO` take a
 * `chargePointId` and load the entity themselves, rather than passing the Exposed DAO through the
 * back stack.
 */
sealed interface Screen {

    @Serializable
    data object ChargePoints : Screen

    @Serializable
    data object Vehicles : Screen

    @Serializable
    data class ChargePoint(
        val chargePointId: Long,
    ) : Screen

    /** Create/edit charge point, shown as a dialog destination. `null` id means create. */
    @Serializable
    data class CreateChargePoint(
        val chargePointId: Long? = null,
    ) : Screen
}
