package com.monta.ocpp.emulator.ocpp.core.model

import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.firmware.DiagnosticsStatusNotificationStatus
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationStatus
import com.monta.ocpp.emulator.chargepoint.core.model.ChargePointMode
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.model.OcppVersion
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Read-only projection of a [com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO] and its
 * connectors, exposed by [com.monta.ocpp.emulator.ocpp.core.service.EmulatorEngine] so the UI can
 * render a charge point without touching Exposed DAOs.
 *
 * Everything the charge-point list, detail, form, display, PBM and send-message screens read is here.
 * OCPP-domain enums and [Instant] carry `@Contextual` (their serializers live outside this module);
 * the engine's own enums ([OcppVersion], [ChargePointMode], [MeterType]) serialize natively.
 */
@Serializable
data class ChargePointSummary(
    val id: Long,
    val name: String,
    val identity: String,
    val ocppVersion: OcppVersion,
    val ocppUrl: String,
    val apiUrl: String,
    val operationMode: ChargePointMode,
    val maxKw: Double,
    val connected: Boolean,
    @Contextual val status: ChargePointStatus,
    @Contextual val statusAt: Instant,
    val averageLatencyMillis: Long,
    val messageCount: Int,
    val firmware: String,
    @Contextual val firmwareStatus: FirmwareStatusNotificationStatus,
    @Contextual val diagnosticsStatus: DiagnosticsStatusNotificationStatus,
    @Contextual val errorCode: ChargePointErrorCode,
    val displayText: String,
    val brand: String,
    val model: String,
    val serial: String,
    val meterType: MeterType,
    val basicAuthPassword: String?,
    // Flattened from ChargePointConfiguration — the only config value any of the 17 screens read
    // (SendMessageWindow's MeterValues default payload).
    val meterValuesSampledData: List<String>,
    val connectors: List<ChargePointConnectorSummary>,
) {
    val connectorCount: Int
        get() = connectors.size
}
