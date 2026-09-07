// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class DERChargingParameters(
    /** DER control functions supported by EV. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType:DERControlFunctions (bitmap) */
    val evSupportedDERControl: List<DERControlEnum>? = null,
    /** Rated maximum injected active power by EV, at specified over-excited power factor (overExcitedPowerFactor). + It can also be defined as the rated maximum discharge power at the rated minimum injected reactive power value. This means that if the EV is providing reactive power support, and it is requested to discharge at max power (e.g. to satisfy an EMS request), the EV may override the request and discharge up to overExcitedMaximumDischargePower to meet the minimum reactive power requirements. + Corresponds to the WOvPF attribute in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVOverExcitedMaximumDischargePower */
    val evOverExcitedMaxDischargePower: Double? = null,
    /** EV power factor when injecting (over excited) the minimum reactive power. + Corresponds to the OvPF attribute in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVOverExcitedPowerFactor */
    val evOverExcitedPowerFactor: Double? = null,
    /** Rated maximum injected active power by EV supported at specified under-excited power factor (EVUnderExcitedPowerFactor). + It can also be defined as the rated maximum dischargePower at the rated minimum absorbed reactive power value. This means that if the EV is providing reactive power support, and it is requested to discharge at max power (e.g. to satisfy an EMS request), the EV may override the request and discharge up to underExcitedMaximumDischargePower to meet the minimum reactive power requirements. + This corresponds to the WUnPF attribute in the IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVUnderExcitedMaximumDischargePower */
    val evUnderExcitedMaxDischargePower: Double? = null,
    /** EV power factor when injecting (under excited) the minimum reactive power. + Corresponds to the OvPF attribute in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVUnderExcitedPowerFactor */
    val evUnderExcitedPowerFactor: Double? = null,
    /** Rated maximum total apparent power, defined by min(EV, EVSE) in va. Corresponds to the VAMaxRtg in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumApparentPower */
    val maxApparentPower: Double? = null,
    /** Rated maximum absorbed apparent power, defined by min(EV, EVSE) in va. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. + Corresponds to the ChaVAMaxRtg in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumChargeApparentPower */
    val maxChargeApparentPower: Double? = null,
    /** Rated maximum absorbed apparent power on phase L2, defined by min(EV, EVSE) in va. Corresponds to the ChaVAMaxRtg in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumChargeApparentPower_L2 */
    val maxChargeApparentPower_L2: Double? = null,
    /** Rated maximum absorbed apparent power on phase L3, defined by min(EV, EVSE) in va. Corresponds to the ChaVAMaxRtg in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumChargeApparentPower_L3 */
    val maxChargeApparentPower_L3: Double? = null,
    /** Rated maximum injected apparent power, defined by min(EV, EVSE) in va. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. + Corresponds to the DisVAMaxRtg in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumDischargeApparentPower */
    val maxDischargeApparentPower: Double? = null,
    /** Rated maximum injected apparent power on phase L2, defined by min(EV, EVSE) in va. + Corresponds to the DisVAMaxRtg in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumDischargeApparentPower_L2 */
    val maxDischargeApparentPower_L2: Double? = null,
    /** Rated maximum injected apparent power on phase L3, defined by min(EV, EVSE) in va. + Corresponds to the DisVAMaxRtg in IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumDischargeApparentPower_L3 */
    val maxDischargeApparentPower_L3: Double? = null,
    /** Rated maximum absorbed reactive power, defined by min(EV, EVSE), in vars. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. + Corresponds to the AvarMax attribute in the IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumChargeReactivePower */
    val maxChargeReactivePower: Double? = null,
    /** Rated maximum absorbed reactive power, defined by min(EV, EVSE), in vars on phase L2. + Corresponds to the AvarMax attribute in the IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumChargeReactivePower_L2 */
    val maxChargeReactivePower_L2: Double? = null,
    /** Rated maximum absorbed reactive power, defined by min(EV, EVSE), in vars on phase L3. + Corresponds to the AvarMax attribute in the IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumChargeReactivePower_L3 */
    val maxChargeReactivePower_L3: Double? = null,
    /** Rated minimum absorbed reactive power, defined by max(EV, EVSE), in vars. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMinimumChargeReactivePower */
    val minChargeReactivePower: Double? = null,
    /** Rated minimum absorbed reactive power, defined by max(EV, EVSE), in vars on phase L2. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMinimumChargeReactivePower_L2 */
    val minChargeReactivePower_L2: Double? = null,
    /** Rated minimum absorbed reactive power, defined by max(EV, EVSE), in vars on phase L3. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMinimumChargeReactivePower_L3 */
    val minChargeReactivePower_L3: Double? = null,
    /** Rated maximum injected reactive power, defined by min(EV, EVSE), in vars. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. + Corresponds to the IvarMax attribute in the IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumDischargeReactivePower */
    val maxDischargeReactivePower: Double? = null,
    /** Rated maximum injected reactive power, defined by min(EV, EVSE), in vars on phase L2. + Corresponds to the IvarMax attribute in the IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumDischargeReactivePower_L2 */
    val maxDischargeReactivePower_L2: Double? = null,
    /** Rated maximum injected reactive power, defined by min(EV, EVSE), in vars on phase L3. + Corresponds to the IvarMax attribute in the IEC 61850. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumDischargeReactivePower_L3 */
    val maxDischargeReactivePower_L3: Double? = null,
    /** Rated minimum injected reactive power, defined by max(EV, EVSE), in vars. + This field represents the sum of all phases, unless values are provided for L2 and L3, in which case this field represents phase L1. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMinimumDischargeReactivePower */
    val minDischargeReactivePower: Double? = null,
    /** Rated minimum injected reactive power, defined by max(EV, EVSE), in var on phase L2. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMinimumDischargeReactivePower_L2 */
    val minDischargeReactivePower_L2: Double? = null,
    /** Rated minimum injected reactive power, defined by max(EV, EVSE), in var on phase L3. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMinimumDischargeReactivePower_L3 */
    val minDischargeReactivePower_L3: Double? = null,
    /** Line voltage supported by EVSE and EV. *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVNominalVoltage */
    val nominalVoltage: Double? = null,
    /** The nominal AC voltage (rms) offset between the Charging Station's electrical connection point and the utility’s point of common coupling. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVNominalVoltageOffset */
    val nominalVoltageOffset: Double? = null,
    /** Maximum AC rms voltage, as defined by min(EV, EVSE) to operate with. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumNominalVoltage */
    val maxNominalVoltage: Double? = null,
    /** Minimum AC rms voltage, as defined by max(EV, EVSE) to operate with. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMinimumNominalVoltage */
    val minNominalVoltage: Double? = null,
    /** Manufacturer of the EV inverter. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVInverterManufacturer */
    val evInverterManufacturer: String? = null,
    /** Model name of the EV inverter. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVInverterModel */
    val evInverterModel: String? = null,
    /** Serial number of the EV inverter. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVInverterSerialNumber */
    val evInverterSerialNumber: String? = null,
    /** Software version of EV inverter. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVInverterSwVersion */
    val evInverterSwVersion: String? = null,
    /** Hardware version of EV inverter. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVInverterHwVersion */
    val evInverterHwVersion: String? = null,
    /** Type of islanding detection method. Only mandatory when islanding detection is required at the site, as set in the ISO 15118 Service Details configuration. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVIslandingDetectionMethod */
    val evIslandingDetectionMethod: List<IslandingDetectionEnum>? = null,
    /** Time after which EV will trip if an island has been detected. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVIslandingTripTime */
    val evIslandingTripTime: Double? = null,
    /** Maximum injected DC current allowed at level 1 charging. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumLevel1DCInjection */
    val evMaximumLevel1DCInjection: Double? = null,
    /** Maximum allowed duration of DC injection at level 1 charging. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVDurationLevel1DCInjection */
    val evDurationLevel1DCInjection: Double? = null,
    /** Maximum injected DC current allowed at level 2 charging. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVMaximumLevel2DCInjection */
    val evMaximumLevel2DCInjection: Double? = null,
    /** Maximum allowed duration of DC injection at level 2 charging. + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVDurationLevel2DCInjection */
    val evDurationLevel2DCInjection: Double? = null,
    /** Measure of the susceptibility of the circuit to reactance, in Siemens (S). + *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVReactiveSusceptance */
    val evReactiveSusceptance: Double? = null,
    /** Total energy value, in Wh, that EV is allowed to provide during the entire V2G session. The value is independent of the V2X Cycling area. Once this value reaches the value of 0, the EV may block any attempt to discharge in order to protect the battery health. *ISO 15118-20*: DER_BPT_AC_CPDReqEnergyTransferModeType: EVSessionTotalDischargeEnergyAvailable */
    val evSessionTotalDischargeEnergyAvailable: Double? = null,
    val customData: CustomData? = null,
)
