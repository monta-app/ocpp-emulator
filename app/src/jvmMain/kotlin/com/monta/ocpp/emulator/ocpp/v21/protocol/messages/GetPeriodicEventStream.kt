// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ConstantStreamData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object GetPeriodicEventStreamFeature : Feature {
    override val name: String = "GetPeriodicEventStream"
    override val requestType: Class<out OcppRequest> = GetPeriodicEventStreamRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetPeriodicEventStreamResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetPeriodicEventStreamRequest(
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetPeriodicEventStreamResponse(
    val constantStreamData: List<ConstantStreamData>? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
