// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ChargingProfileCriterion(
    val chargingProfilePurpose: ChargingProfilePurposeEnum? = null,
    /** Value determining level in hierarchy stack of profiles. Higher values have precedence over lower values. Lowest level is 0. */
    val stackLevel: Int? = null,
    /** List of all the chargingProfileIds requested. Any ChargingProfile that matches one of these profiles will be reported. If omitted, the Charging Station SHALL not filter on chargingProfileId. This field SHALL NOT contain more ids than set in &lt;&lt;configkey-charging-profile-entries,ChargingProfileEntries.maxLimit&gt;&gt; */
    val chargingProfileId: List<Int>? = null,
    /** For which charging limit sources, charging profiles SHALL be reported. If omitted, the Charging Station SHALL not filter on chargingLimitSource. Values defined in Appendix as ChargingLimitSourceEnumStringType. */
    val chargingLimitSource: List<String>? = null,
    val customData: CustomData? = null,
)
