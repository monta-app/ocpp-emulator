// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class Modem(
    /** This contains the ICCID of the modem’s SIM card. */
    val iccid: String? = null,
    /** This contains the IMSI of the modem’s SIM card. */
    val imsi: String? = null,
    val customData: CustomData? = null,
)
