// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ReportData
import java.time.ZonedDateTime

object NotifyReportFeature : Feature {
    override val name: String = "NotifyReport"
    override val requestType: Class<out OcppRequest> = NotifyReportRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyReportResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyReportRequest(
    /** The id of the GetReportRequest or GetBaseReportRequest that requested this report */
    val requestId: Int,
    /** Timestamp of the moment this message was generated at the Charging Station. */
    val generatedAt: ZonedDateTime,
    /** Sequence number of this message. First message starts at 0. */
    val seqNo: Int,
    val reportData: List<ReportData>? = null,
    /** “to be continued” indicator. Indicates whether another part of the report follows in an upcoming notifyReportRequest message. Default value when omitted is false. */
    val tbc: Boolean? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyReportResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
