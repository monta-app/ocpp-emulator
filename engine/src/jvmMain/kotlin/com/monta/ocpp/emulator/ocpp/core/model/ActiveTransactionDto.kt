package com.monta.ocpp.emulator.ocpp.core.model

import java.time.Instant

/**
 * Read-only projection of the active [com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO]
 * on a connector. Plain, typed, DAO-free so [com.monta.ocpp.emulator.ocpp.core.service.EmulatorEngine]
 * can hand the running transaction to the UI without leaking Exposed types.
 */
data class ActiveTransactionDto(
    val id: Long,
    val externalId: Int,
    val idTag: String,
    val connectorPosition: Int,
    val startTime: Instant,
    val startMeter: Double,
    val endMeter: Double,
)
