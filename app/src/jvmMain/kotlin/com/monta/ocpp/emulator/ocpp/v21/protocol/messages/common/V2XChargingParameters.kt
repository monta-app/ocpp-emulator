// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class V2XChargingParameters(
    /** Minimum charge power in W, defined by max(EV, EVSE). This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMinimumChargePower */
    val minChargePower: Double? = null,
    /** Minimum charge power on phase L2 in W, defined by max(EV, EVSE). Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMinimumChargePower_L2 */
    val minChargePower_L2: Double? = null,
    /** Minimum charge power on phase L3 in W, defined by max(EV, EVSE). Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMinimumChargePower_L3 */
    val minChargePower_L3: Double? = null,
    /** Maximum charge (absorbed) power in W, defined by min(EV, EVSE) at unity power factor. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. It corresponds to the ChaWMax attribute in the IEC 61850. It is usually equivalent to the rated apparent power of the EV when discharging (ChaVAMax) in IEC 61850. + Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMaximumChargePower */
    val maxChargePower: Double? = null,
    /** Maximum charge power on phase L2 in W, defined by min(EV, EVSE) Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMaximumChargePower_L2 */
    val maxChargePower_L2: Double? = null,
    /** Maximum charge power on phase L3 in W, defined by min(EV, EVSE) Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMaximumChargePower_L3 */
    val maxChargePower_L3: Double? = null,
    /** Minimum discharge (injected) power in W, defined by max(EV, EVSE) at unity power factor. Value &gt;= 0. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. + It corresponds to the WMax attribute in the IEC 61850. It is usually equivalent to the rated apparent power of the EV when discharging (VAMax attribute in the IEC 61850). Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMinimumDischargePower */
    val minDischargePower: Double? = null,
    /** Minimum discharge power on phase L2 in W, defined by max(EV, EVSE). Value &gt;= 0. Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMinimumDischargePower_L2 */
    val minDischargePower_L2: Double? = null,
    /** Minimum discharge power on phase L3 in W, defined by max(EV, EVSE). Value &gt;= 0. Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMinimumDischargePower_L3 */
    val minDischargePower_L3: Double? = null,
    /** Maximum discharge (injected) power in W, defined by min(EV, EVSE) at unity power factor. Value &gt;= 0. This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMaximumDischargePower */
    val maxDischargePower: Double? = null,
    /** Maximum discharge power on phase L2 in W, defined by min(EV, EVSE). Value &gt;= 0. Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMaximumDischargePowe_L2 */
    val maxDischargePower_L2: Double? = null,
    /** Maximum discharge power on phase L3 in W, defined by min(EV, EVSE). Value &gt;= 0. Relates to: *ISO 15118-20*: BPT_AC/DC_CPDReqEnergyTransferModeType: EVMaximumDischargePower_L3 */
    val maxDischargePower_L3: Double? = null,
    /** Minimum charge current in A, defined by max(EV, EVSE) Relates to: *ISO 15118-20*: BPT_DC_CPDReqEnergyTransferModeType: EVMinimumChargeCurrent */
    val minChargeCurrent: Double? = null,
    /** Maximum charge current in A, defined by min(EV, EVSE) Relates to: *ISO 15118-20*: BPT_DC_CPDReqEnergyTransferModeType: EVMaximumChargeCurrent */
    val maxChargeCurrent: Double? = null,
    /** Minimum discharge current in A, defined by max(EV, EVSE). Value &gt;= 0. Relates to: *ISO 15118-20*: BPT_DC_CPDReqEnergyTransferModeType: EVMinimumDischargeCurrent */
    val minDischargeCurrent: Double? = null,
    /** Maximum discharge current in A, defined by min(EV, EVSE). Value &gt;= 0. Relates to: *ISO 15118-20*: BPT_DC_CPDReqEnergyTransferModeType: EVMaximumDischargeCurrent */
    val maxDischargeCurrent: Double? = null,
    /** Minimum voltage in V, defined by max(EV, EVSE) Relates to: *ISO 15118-20*: BPT_DC_CPDReqEnergyTransferModeType: EVMinimumVoltage */
    val minVoltage: Double? = null,
    /** Maximum voltage in V, defined by min(EV, EVSE) Relates to: *ISO 15118-20*: BPT_DC_CPDReqEnergyTransferModeType: EVMaximumVoltage */
    val maxVoltage: Double? = null,
    /** Energy to requested state of charge in Wh Relates to: *ISO 15118-20*: Dynamic/Scheduled_SEReqControlModeType: EVTargetEnergyRequest */
    val evTargetEnergyRequest: Double? = null,
    /** Energy to minimum allowed state of charge in Wh Relates to: *ISO 15118-20*: Dynamic/Scheduled_SEReqControlModeType: EVMinimumEnergyRequest */
    val evMinEnergyRequest: Double? = null,
    /** Energy to maximum state of charge in Wh Relates to: *ISO 15118-20*: Dynamic/Scheduled_SEReqControlModeType: EVMaximumEnergyRequest */
    val evMaxEnergyRequest: Double? = null,
    /** Energy (in Wh) to minimum state of charge for cycling (V2X) activity. Positive value means that current state of charge is below V2X range. Relates to: *ISO 15118-20*: Dynamic_SEReqControlModeType: EVMinimumV2XEnergyRequest */
    val evMinV2XEnergyRequest: Double? = null,
    /** Energy (in Wh) to maximum state of charge for cycling (V2X) activity. Negative value indicates that current state of charge is above V2X range. Relates to: *ISO 15118-20*: Dynamic_SEReqControlModeType: EVMaximumV2XEnergyRequest */
    val evMaxV2XEnergyRequest: Double? = null,
    /** Target state of charge at departure as percentage. Relates to: *ISO 15118-20*: BPT_DC_CPDReqEnergyTransferModeType: TargetSOC */
    val targetSoC: Int? = null,
    val customData: CustomData? = null,
)
