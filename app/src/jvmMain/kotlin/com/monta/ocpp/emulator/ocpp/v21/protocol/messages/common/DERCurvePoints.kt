// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class DERCurvePoints(
    /** The data value of the X-axis (independent) variable, depending on the curve type. */
    val x: Double,
    /** The data value of the Y-axis (dependent) variable, depending on the &lt;&lt;cmn_derunitenumtype&gt;&gt; of the curve. If _y_ is power factor, then a positive value means DER is absorbing reactive power (under-excited), a negative value when DER is injecting reactive power (over-excited). */
    val y: Double,
    val customData: CustomData? = null,
)
