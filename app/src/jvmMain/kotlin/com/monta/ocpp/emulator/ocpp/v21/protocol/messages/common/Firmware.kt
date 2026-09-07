// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class Firmware(
    /** URI defining the origin of the firmware. */
    val location: String,
    /** Date and time at which the firmware shall be retrieved. */
    val retrieveDateTime: ZonedDateTime,
    /** Date and time at which the firmware shall be installed. */
    val installDateTime: ZonedDateTime? = null,
    /** Certificate with which the firmware was signed. PEM encoded X.509 certificate. */
    val signingCertificate: String? = null,
    /** Base64 encoded firmware signature. */
    val signature: String? = null,
    val customData: CustomData? = null,
)
