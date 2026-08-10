// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MessageInfo

object NotifyDisplayMessagesFeature : Feature {
    override val name: String = "NotifyDisplayMessages"
    override val requestType: Class<out OcppRequest> = NotifyDisplayMessagesRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyDisplayMessagesResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyDisplayMessagesRequest(
    /** The id of the &lt;&lt;getdisplaymessagesrequest,GetDisplayMessagesRequest&gt;&gt; that requested this message. */
    val requestId: Int,
    val messageInfo: List<MessageInfo>? = null,
    /** "to be continued" indicator. Indicates whether another part of the report follows in an upcoming NotifyDisplayMessagesRequest message. Default value when omitted is false. */
    val tbc: Boolean? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyDisplayMessagesResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
