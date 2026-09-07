// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class EVSE(
    /** EVSE Identifier. This contains a number (&gt; 0) designating an EVSE of the Charging Station. */
    val id: Int,
    /** An id to designate a specific connector (on an EVSE) by connector index number. */
    val connectorId: Int? = null,
    val customData: CustomData? = null,
)
