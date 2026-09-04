// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import java.time.ZonedDateTime

object NotifyCustomerInformationFeature : Feature {
    override val name: String = "NotifyCustomerInformation"
    override val requestType: Class<out OcppRequest> = NotifyCustomerInformationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyCustomerInformationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyCustomerInformationRequest(
    /** (Part of) the requested data. No format specified in which the data is returned. Should be human readable. */
    val `data`: String,
    /** Sequence number of this message. First message starts at 0. */
    val seqNo: Int,
    /** Timestamp of the moment this message was generated at the Charging Station. */
    val generatedAt: ZonedDateTime,
    /** The Id of the request. */
    val requestId: Int,
    /** “to be continued” indicator. Indicates whether another part of the monitoringData follows in an upcoming notifyMonitoringReportRequest message. Default value when omitted is false. */
    val tbc: Boolean? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyCustomerInformationResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
