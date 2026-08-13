// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ChargingSchedulePeriod(
    /** Start of the period, in seconds from the start of schedule. The value of StartPeriod also defines the stop time of the previous period. */
    val startPeriod: Int,
    /** Optional only when not required by the _operationMode_, as in CentralSetpoint, ExternalSetpoint, ExternalLimits, LocalFrequency, LocalLoadBalancing. + Charging rate limit during the schedule period, in the applicable _chargingRateUnit_. This SHOULD be a non-negative value; a negative value is only supported for backwards compatibility with older systems that use a negative value to specify a discharging limit. When using _chargingRateUnit_ = `W`, this field represents the sum of the power of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. */
    val limit: Double? = null,
    /** *(2.1)* Charging rate limit on phase L2 in the applicable _chargingRateUnit_. */
    val limit_L2: Double? = null,
    /** *(2.1)* Charging rate limit on phase L3 in the applicable _chargingRateUnit_. */
    val limit_L3: Double? = null,
    /** The number of phases that can be used for charging. + For a DC EVSE this field should be omitted. + For an AC EVSE a default value of _numberPhases_ = 3 will be assumed if the field is absent. */
    val numberPhases: Int? = null,
    /** Values: 1..3, Used if numberPhases=1 and if the EVSE is capable of switching the phase connected to the EV, i.e. ACPhaseSwitchingSupported is defined and true. It’s not allowed unless both conditions above are true. If both conditions are true, and phaseToUse is omitted, the Charging Station / EVSE will make the selection on its own. */
    val phaseToUse: Int? = null,
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
    /** *(2.1)* If true, the EV should attempt to keep the BMS preconditioned for this time interval. */
    val preconditioningRequest: Boolean? = null,
    /** *(2.1)* If true, the EVSE must turn off power electronics/modules associated with this transaction. Default value when absent is false. */
    val evseSleep: Boolean? = null,
    /** *(2.1)* Power value that, when present, is used as a baseline on top of which values from _v2xFreqWattCurve_ and _v2xSignalWattCurve_ are added. */
    val v2xBaseline: Double? = null,
    val operationMode: OperationModeEnum? = null,
    val v2xFreqWattCurve: List<V2XFreqWattPoint>? = null,
    val v2xSignalWattCurve: List<V2XSignalWattPoint>? = null,
    val customData: CustomData? = null,
)
