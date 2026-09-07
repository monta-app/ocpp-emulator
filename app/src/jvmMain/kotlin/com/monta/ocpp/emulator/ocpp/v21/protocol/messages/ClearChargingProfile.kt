// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearChargingProfile
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearChargingProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object ClearChargingProfileFeature : Feature {
    override val name: String = "ClearChargingProfile"
    override val requestType: Class<out OcppRequest> = ClearChargingProfileRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ClearChargingProfileResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearChargingProfileRequest(
    /** The Id of the charging profile to clear. */
    val chargingProfileId: Int? = null,
    val chargingProfileCriteria: ClearChargingProfile? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearChargingProfileResponse(
    val status: ClearChargingProfileStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
