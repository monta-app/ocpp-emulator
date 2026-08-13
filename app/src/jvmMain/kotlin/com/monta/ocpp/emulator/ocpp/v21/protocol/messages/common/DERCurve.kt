// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class DERCurve(
    val curveData: List<DERCurvePoints>,
    /** Priority of curve (0=highest) */
    val priority: Int,
    val yUnit: DERUnitEnum,
    val hysteresis: Hysteresis? = null,
    val reactivePowerParams: ReactivePowerParams? = null,
    val voltageParams: VoltageParams? = null,
    /** Open loop response time, the time to ramp up to 90% of the new target in response to the change in voltage, in seconds. A value of 0 is used to mean no limit. When not present, the device should follow its default behavior. */
    val responseTime: Double? = null,
    /** Point in time when this curve will become activated. Only absent when _default_ is true. */
    val startTime: ZonedDateTime? = null,
    /** Duration in seconds that this curve will be active. Only absent when _default_ is true. */
    val duration: Double? = null,
    val customData: CustomData? = null,
)
