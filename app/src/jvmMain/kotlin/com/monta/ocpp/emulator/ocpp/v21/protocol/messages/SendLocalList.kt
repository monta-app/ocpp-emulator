// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.AuthorizationData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SendLocalListStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.UpdateEnum

object SendLocalListFeature : Feature {
    override val name: String = "SendLocalList"
    override val requestType: Class<out OcppRequest> = SendLocalListRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = SendLocalListResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SendLocalListRequest(
    /** In case of a full update this is the version number of the full list. In case of a differential update it is the version number of the list after the update has been applied. */
    val versionNumber: Int,
    val updateType: UpdateEnum,
    val localAuthorizationList: List<AuthorizationData>? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SendLocalListResponse(
    val status: SendLocalListStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
