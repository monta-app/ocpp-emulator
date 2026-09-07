// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class TotalUsage(
    val energy: Double,
    /** Total duration of the charging session (including the duration of charging and not charging), in seconds. */
    val chargingTime: Int,
    /** Total duration of the charging session where the EV was not charging (no energy was transferred between EVSE and EV), in seconds. */
    val idleTime: Int,
    /** Total time of reservation in seconds. */
    val reservationTime: Int? = null,
    val customData: CustomData? = null,
)
