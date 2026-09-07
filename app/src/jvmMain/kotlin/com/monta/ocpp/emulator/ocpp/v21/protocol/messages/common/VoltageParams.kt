// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class VoltageParams(
    /** EN 50549-1 chapter 4.9.3.4 Voltage threshold for the 10 min time window mean value monitoring. The 10 min mean is recalculated up to every 3 s. If the present voltage is above this threshold for more than the time defined by _hv10MinMeanValue_, the EV must trip. This value is mandatory if _hv10MinMeanTripDelay_ is set. */
    val hv10MinMeanValue: Double? = null,
    /** Time for which the voltage is allowed to stay above the 10 min mean value. After this time, the EV must trip. This value is mandatory if OverVoltageMeanValue10min is set. */
    val hv10MinMeanTripDelay: Double? = null,
    val powerDuringCessation: PowerDuringCessationEnum? = null,
    val customData: CustomData? = null,
)
