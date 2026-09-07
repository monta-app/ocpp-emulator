// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DisplayMessageStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MessageInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object SetDisplayMessageFeature : Feature {
    override val name: String = "SetDisplayMessage"
    override val requestType: Class<out OcppRequest> = SetDisplayMessageRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = SetDisplayMessageResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetDisplayMessageRequest(
    val message: MessageInfo,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetDisplayMessageResponse(
    val status: DisplayMessageStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
