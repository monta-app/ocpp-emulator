// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ClearChargingProfile(
    /** Specifies the id of the EVSE for which to clear charging profiles. An evseId of zero (0) specifies the charging profile for the overall Charging Station. Absence of this parameter means the clearing applies to all charging profiles that match the other criteria in the request. */
    val evseId: Int? = null,
    val chargingProfilePurpose: ChargingProfilePurposeEnum? = null,
    /** Specifies the stackLevel for which charging profiles will be cleared, if they meet the other criteria in the request. */
    val stackLevel: Int? = null,
    val customData: CustomData? = null,
)
