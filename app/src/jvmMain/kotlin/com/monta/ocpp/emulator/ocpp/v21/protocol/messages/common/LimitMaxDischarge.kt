// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class LimitMaxDischarge(
    /** Priority of setting (0=highest) */
    val priority: Int,
    /** Only for PowerMonitoring. + The value specifies a percentage (0 to 100) of the rated maximum discharge power of EV. The PowerMonitoring curve becomes active when power exceeds this percentage. */
    val pctMaxDischargePower: Double? = null,
    val powerMonitoringMustTrip: DERCurve? = null,
    /** Time when this setting becomes active */
    val startTime: ZonedDateTime? = null,
    /** Duration in seconds that this setting is active */
    val duration: Double? = null,
    val customData: CustomData? = null,
)
