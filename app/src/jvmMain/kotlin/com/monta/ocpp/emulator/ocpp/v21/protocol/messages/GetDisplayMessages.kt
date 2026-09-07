// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetDisplayMessagesStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MessagePriorityEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MessageStateEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetDisplayMessagesFeature : Feature {
    override val name: String = "GetDisplayMessages"
    override val requestType: Class<out OcppRequest> = GetDisplayMessagesRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetDisplayMessagesResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetDisplayMessagesRequest(
    /** The Id of this request. */
    val requestId: Int,
    /** If provided the Charging Station shall return Display Messages of the given ids. This field SHALL NOT contain more ids than set in &lt;&lt;configkey-number-of-display-messages,NumberOfDisplayMessages.maxLimit&gt;&gt; */
    val id: List<Int>? = null,
    val priority: MessagePriorityEnum? = null,
    val state: MessageStateEnum? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetDisplayMessagesResponse(
    val status: GetDisplayMessagesStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
