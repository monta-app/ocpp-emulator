// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import java.time.ZonedDateTime

object NotifyDERStartStopFeature : Feature {
    override val name: String = "NotifyDERStartStop"
    override val requestType: Class<out OcppRequest> = NotifyDERStartStopRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyDERStartStopResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyDERStartStopRequest(
    /** Id of the started or stopped DER control. Corresponds to the _controlId_ of the SetDERControlRequest. */
    val controlId: String,
    /** True if DER control has started. False if it has ended. */
    val started: Boolean,
    /** Time of start or end of event. */
    val timestamp: ZonedDateTime,
    /** List of controlIds that are superseded as a result of this control starting. */
    val supersededIds: List<String>? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyDERStartStopResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
