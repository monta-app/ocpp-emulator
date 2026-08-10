// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingScheduleUpdate
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object PullDynamicScheduleUpdateFeature : Feature {
    override val name: String = "PullDynamicScheduleUpdate"
    override val requestType: Class<out OcppRequest> = PullDynamicScheduleUpdateRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = PullDynamicScheduleUpdateResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class PullDynamicScheduleUpdateRequest(
    /** Id of charging profile to update. */
    val chargingProfileId: Int,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class PullDynamicScheduleUpdateResponse(
    val status: ChargingProfileStatusEnum,
    val scheduleUpdate: ChargingScheduleUpdate? = null,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
