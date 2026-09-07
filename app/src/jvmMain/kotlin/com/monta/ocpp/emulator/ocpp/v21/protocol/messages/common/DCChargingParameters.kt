// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class DCChargingParameters(
    /** Maximum current (in A) supported by the electric vehicle. Includes cable capacity. Relates to: + *ISO 15118-2*: DC_EVChargeParameterType:EVMaximumCurrentLimit */
    val evMaxCurrent: Double,
    /** Maximum voltage supported by the electric vehicle. Relates to: + *ISO 15118-2*: DC_EVChargeParameterType: EVMaximumVoltageLimit */
    val evMaxVoltage: Double,
    /** Maximum power (in W) supported by the electric vehicle. Required for DC charging. Relates to: + *ISO 15118-2*: DC_EVChargeParameterType: EVMaximumPowerLimit */
    val evMaxPower: Double? = null,
    /** Capacity of the electric vehicle battery (in Wh). Relates to: + *ISO 15118-2*: DC_EVChargeParameterType: EVEnergyCapacity */
    val evEnergyCapacity: Double? = null,
    /** Amount of energy requested (in Wh). This inludes energy required for preconditioning. Relates to: + *ISO 15118-2*: DC_EVChargeParameterType: EVEnergyRequest */
    val energyAmount: Double? = null,
    /** Energy available in the battery (in percent of the battery capacity) Relates to: + *ISO 15118-2*: DC_EVChargeParameterType: DC_EVStatus: EVRESSSOC */
    val stateOfCharge: Int? = null,
    /** Percentage of SoC at which the EV considers the battery fully charged. (possible values: 0 - 100) Relates to: + *ISO 15118-2*: DC_EVChargeParameterType: FullSOC */
    val fullSoC: Int? = null,
    /** Percentage of SoC at which the EV considers a fast charging process to end. (possible values: 0 - 100) Relates to: + *ISO 15118-2*: DC_EVChargeParameterType: BulkSOC */
    val bulkSoC: Int? = null,
    val customData: CustomData? = null,
)
