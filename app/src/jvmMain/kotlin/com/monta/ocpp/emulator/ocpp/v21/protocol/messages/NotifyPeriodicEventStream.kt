// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import java.time.ZonedDateTime

object NotifyPeriodicEventStreamFeature : Feature {
    override val name: String = "NotifyPeriodicEventStream"
    override val requestType: Class<out OcppRequest> = NotifyPeriodicEventStreamRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyPeriodicEventStreamResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyPeriodicEventStreamRequest(
    val `data`: List<String>,
    /** Id of stream. */
    val id: Int,
    /** Number of data elements still pending to be sent. */
    val pending: Int,
    /** Base timestamp to add to time offset of values. */
    val basetime: ZonedDateTime,
    val customData: CustomData? = null,
) : OcppRequest

data class NotifyPeriodicEventStreamResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
