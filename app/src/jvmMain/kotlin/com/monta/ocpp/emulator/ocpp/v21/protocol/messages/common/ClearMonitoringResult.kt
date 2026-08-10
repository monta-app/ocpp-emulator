// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ClearMonitoringResult(
    val status: ClearMonitoringStatusEnum,
    /** Id of the monitor of which a clear was requested. */
    val id: Int,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
)
