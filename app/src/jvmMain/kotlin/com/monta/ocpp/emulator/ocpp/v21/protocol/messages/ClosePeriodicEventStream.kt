// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object ClosePeriodicEventStreamFeature : Feature {
    override val name: String = "ClosePeriodicEventStream"
    override val requestType: Class<out OcppRequest> = ClosePeriodicEventStreamRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ClosePeriodicEventStreamResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClosePeriodicEventStreamRequest(
    /** Id of stream to close. */
    val id: Int,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClosePeriodicEventStreamResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
