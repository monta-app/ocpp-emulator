// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class LogParameters(
    /** The URL of the location at the remote system where the log should be stored. */
    val remoteLocation: String,
    /** This contains the date and time of the oldest logging information to include in the diagnostics. */
    val oldestTimestamp: ZonedDateTime? = null,
    /** This contains the date and time of the latest logging information to include in the diagnostics. */
    val latestTimestamp: ZonedDateTime? = null,
    val customData: CustomData? = null,
)
