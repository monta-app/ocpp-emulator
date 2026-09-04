// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingProfileCriterion
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetChargingProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetChargingProfilesFeature : Feature {
    override val name: String = "GetChargingProfiles"
    override val requestType: Class<out OcppRequest> = GetChargingProfilesRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetChargingProfilesResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetChargingProfilesRequest(
    /** Reference identification that is to be used by the Charging Station in the &lt;&lt;reportchargingprofilesrequest, ReportChargingProfilesRequest&gt;&gt; when provided. */
    val requestId: Int,
    val chargingProfile: ChargingProfileCriterion,
    /** For which EVSE installed charging profiles SHALL be reported. If 0, only charging profiles installed on the Charging Station itself (the grid connection) SHALL be reported. If omitted, all installed charging profiles SHALL be reported. + Reported charging profiles SHALL match the criteria in field _chargingProfile_. */
    val evseId: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetChargingProfilesResponse(
    val status: GetChargingProfileStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
