// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import java.time.ZonedDateTime

object HeartbeatFeature : Feature {
    override val name: String = "Heartbeat"
    override val requestType: Class<out OcppRequest> = HeartbeatRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = HeartbeatResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class HeartbeatRequest(
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class HeartbeatResponse(
    /** Contains the current time of the CSMS. */
    val currentTime: ZonedDateTime,
    val customData: CustomData? = null,
) : OcppConfirmation
