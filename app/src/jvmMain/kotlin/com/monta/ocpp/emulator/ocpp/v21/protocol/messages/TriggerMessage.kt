// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.EVSE
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MessageTriggerEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TriggerMessageStatusEnum

object TriggerMessageFeature : Feature {
    override val name: String = "TriggerMessage"
    override val requestType: Class<out OcppRequest> = TriggerMessageRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = TriggerMessageResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class TriggerMessageRequest(
    val requestedMessage: MessageTriggerEnum,
    val evse: EVSE? = null,
    /** *(2.1)* When _requestedMessage_ = `CustomTrigger` this will trigger sending the corresponding message in field _customTrigger_, if supported by Charging Station. */
    val customTrigger: String? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class TriggerMessageResponse(
    val status: TriggerMessageStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
