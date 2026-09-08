package com.monta.ocpp.emulator.ocpp.core.model

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Read-only projection of a [com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO]
 * plus the computed values the connector UI reads (`meterWh`, the active transaction). Plain, typed
 * and DAO-free.
 *
 * The OCPP-domain enums ([ChargePointStatus], [ChargePointErrorCode]) and [Instant] carry
 * `@Contextual` because their serializers live outside this module; the field types stay the real
 * enums (no stringly-typed status).
 */
@Serializable
data class ChargePointConnectorSummary(
    val id: Long,
    val chargePointId: Long,
    val position: Int,
    @Contextual val status: ChargePointStatus,
    @Contextual val statusAt: Instant,
    @Contextual val errorCode: ChargePointErrorCode,
    val vendorId: String?,
    val vendorErrorCode: String?,
    val statusInfo: String?,
    val carState: CarState,
    val locked: Boolean,
    val maxKw: Double,
    val kw: Double,
    val vehicleMaxAmpsPerPhase: Double,
    val vehicleNumberPhases: Int,
    val meterWh: Double,
    val activeTransaction: ActiveTransactionSummary?,
) {
    val hasActiveTransaction: Boolean
        get() = activeTransaction != null
}
