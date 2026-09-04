// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ACChargingParameters(
    /** Amount of energy requested (in Wh). This includes energy required for preconditioning. Relates to: + *ISO 15118-2*: AC_EVChargeParameterType: EAmount + *ISO 15118-20*: Dynamic/Scheduled_SEReqControlModeType: EVTargetEnergyRequest */
    val energyAmount: Double,
    /** Minimum current (amps) supported by the electric vehicle (per phase). Relates to: + *ISO 15118-2*: AC_EVChargeParameterType: EVMinCurrent */
    val evMinCurrent: Double,
    /** Maximum current (amps) supported by the electric vehicle (per phase). Includes cable capacity. Relates to: + *ISO 15118-2*: AC_EVChargeParameterType: EVMaxCurrent */
    val evMaxCurrent: Double,
    /** Maximum voltage supported by the electric vehicle. Relates to: + *ISO 15118-2*: AC_EVChargeParameterType: EVMaxVoltage */
    val evMaxVoltage: Double,
    val customData: CustomData? = null,
)
