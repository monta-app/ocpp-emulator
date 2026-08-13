// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class UnitOfMeasure(
    /** Unit of the value. Default = "Wh" if the (default) measurand is an "Energy" type. This field SHALL use a value from the list Standardized Units of Measurements in Part 2 Appendices. If an applicable unit is available in that list, otherwise a "custom" unit might be used. */
    val unit: String? = null,
    /** Multiplier, this value represents the exponent to base 10. I.e. multiplier 3 means 10 raised to the 3rd power. Default is 0. + The _multiplier_ only multiplies the value of the measurand. It does not specify a conversion between units, for example, kW and W. */
    val multiplier: Int? = null,
    val customData: CustomData? = null,
)
