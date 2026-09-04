// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ReactivePowerParams(
    /** Only for VoltVar curve: The nominal ac voltage (rms) adjustment to the voltage curve points for Volt-Var curves (percentage). */
    val vRef: Double? = null,
    /** Only for VoltVar: Enable/disable autonomous VRef adjustment */
    val autonomousVRefEnable: Boolean? = null,
    /** Only for VoltVar: Adjustment range for VRef time constant */
    val autonomousVRefTimeConstant: Double? = null,
    val customData: CustomData? = null,
)
