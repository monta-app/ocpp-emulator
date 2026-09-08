package com.monta.ocpp.emulator.ocpp.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Read-only projection of the active [com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO]
 * on a connector. Plain, typed, DAO-free so [com.monta.ocpp.emulator.ocpp.core.service.EmulatorEngine]
 * can hand the running transaction to the UI without leaking Exposed types.
 */
@Serializable
data class ActiveTransactionSummary(
    val id: Long,
    val externalId: Int,
    val idTag: String,
    val connectorPosition: Int,
    @Contextual val startTime: Instant,
    val startMeter: Double,
    val endMeter: Double,
)
