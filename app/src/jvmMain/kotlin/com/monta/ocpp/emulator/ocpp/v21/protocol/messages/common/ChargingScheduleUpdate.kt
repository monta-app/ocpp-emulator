// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ChargingScheduleUpdate(
    /** Optional only when not required by the _operationMode_, as in CentralSetpoint, ExternalSetpoint, ExternalLimits, LocalFrequency, LocalLoadBalancing. + Charging rate limit during the schedule period, in the applicable _chargingRateUnit_. This SHOULD be a non-negative value; a negative value is only supported for backwards compatibility with older systems that use a negative value to specify a discharging limit. For AC this field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. */
    val limit: Double? = null,
    /** *(2.1)* Charging rate limit on phase L2 in the applicable _chargingRateUnit_. */
    val limit_L2: Double? = null,
    /** *(2.1)* Charging rate limit on phase L3 in the applicable _chargingRateUnit_. */
    val limit_L3: Double? = null,
    /** *(2.1)* Limit in _chargingRateUnit_ that the EV is allowed to discharge with. Note, these are negative values in order to be consistent with _setpoint_, which can be positive and negative. + For AC this field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. */
    val dischargeLimit: Double? = null,
    /** *(2.1)* Limit in _chargingRateUnit_ on phase L2 that the EV is allowed to discharge with. */
    val dischargeLimit_L2: Double? = null,
    /** *(2.1)* Limit in _chargingRateUnit_ on phase L3 that the EV is allowed to discharge with. */
    val dischargeLimit_L3: Double? = null,
    /** *(2.1)* Setpoint in _chargingRateUnit_ that the EV should follow as close as possible. Use negative values for discharging. + When a limit and/or _dischargeLimit_ are given the overshoot when following _setpoint_ must remain within these values. This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. */
    val setpoint: Double? = null,
    /** *(2.1)* Setpoint in _chargingRateUnit_ that the EV should follow on phase L2 as close as possible. */
    val setpoint_L2: Double? = null,
    /** *(2.1)* Setpoint in _chargingRateUnit_ that the EV should follow on phase L3 as close as possible. */
    val setpoint_L3: Double? = null,
    /** *(2.1)* Setpoint for reactive power (or current) in _chargingRateUnit_ that the EV should follow as closely as possible. Positive values for inductive, negative for capacitive reactive power or current. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. */
    val setpointReactive: Double? = null,
    /** *(2.1)* Setpoint for reactive power (or current) in _chargingRateUnit_ that the EV should follow on phase L2 as closely as possible. */
    val setpointReactive_L2: Double? = null,
    /** *(2.1)* Setpoint for reactive power (or current) in _chargingRateUnit_ that the EV should follow on phase L3 as closely as possible. */
    val setpointReactive_L3: Double? = null,
    val customData: CustomData? = null,
)
