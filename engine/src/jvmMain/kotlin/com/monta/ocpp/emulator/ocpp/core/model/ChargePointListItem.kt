package com.monta.ocpp.emulator.ocpp.core.model

import com.monta.ocpp.emulator.chargepoint.core.model.ChargePointMode
import com.monta.ocpp.emulator.chargepoint.core.model.OcppVersion

/**
 * Read-only projection of a [com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO] holding
 * only the scalar columns the charge-point *list* views read — the table on the charge points screen
 * and the connected-charge-point tab strip on the detail page.
 *
 * Deliberately carries no connectors. [ChargePointSummary] traverses the connector rows, each
 * connector's active transaction and a `sumOf` over its transaction history; the list views read
 * none of that, and their flow re-emits on every charge-point row change (which includes the
 * `messageCount` and `averageLatencyMillis` columns that tick on every OCPP message). Projecting the
 * whole graph to render a name and a status badge is why this type exists.
 */
data class ChargePointListItem(
    val id: Long,
    val name: String,
    val identity: String,
    val ocppVersion: OcppVersion,
    val ocppUrl: String,
    val operationMode: ChargePointMode,
    val maxKw: Double,
    val connected: Boolean,
)
