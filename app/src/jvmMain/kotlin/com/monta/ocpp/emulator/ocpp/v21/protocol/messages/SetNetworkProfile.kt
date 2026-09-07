// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.NetworkConnectionProfile
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SetNetworkProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object SetNetworkProfileFeature : Feature {
    override val name: String = "SetNetworkProfile"
    override val requestType: Class<out OcppRequest> = SetNetworkProfileRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = SetNetworkProfileResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetNetworkProfileRequest(
    /** Slot in which the configuration should be stored. */
    val configurationSlot: Int,
    val connectionData: NetworkConnectionProfile,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetNetworkProfileResponse(
    val status: SetNetworkProfileStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
