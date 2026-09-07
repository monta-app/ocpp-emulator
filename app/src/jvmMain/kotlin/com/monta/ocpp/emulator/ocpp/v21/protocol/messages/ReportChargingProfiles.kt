// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingProfile
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object ReportChargingProfilesFeature : Feature {
    override val name: String = "ReportChargingProfiles"
    override val requestType: Class<out OcppRequest> = ReportChargingProfilesRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ReportChargingProfilesResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ReportChargingProfilesRequest(
    /** Id used to match the &lt;&lt;getchargingprofilesrequest, GetChargingProfilesRequest&gt;&gt; message with the resulting ReportChargingProfilesRequest messages. When the CSMS provided a requestId in the &lt;&lt;getchargingprofilesrequest, GetChargingProfilesRequest&gt;&gt;, this field SHALL contain the same value. */
    val requestId: Int,
    /** Source that has installed this charging profile. Values defined in Appendix as ChargingLimitSourceEnumStringType. */
    val chargingLimitSource: String,
    val chargingProfile: List<ChargingProfile>,
    /** The evse to which the charging profile applies. If evseId = 0, the message contains an overall limit for the Charging Station. */
    val evseId: Int,
    /** To Be Continued. Default value when omitted: false. false indicates that there are no further messages as part of this report. */
    val tbc: Boolean? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ReportChargingProfilesResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
