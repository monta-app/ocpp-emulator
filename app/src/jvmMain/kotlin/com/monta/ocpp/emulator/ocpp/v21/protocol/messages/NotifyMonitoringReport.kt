// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MonitoringData
import java.time.ZonedDateTime

object NotifyMonitoringReportFeature : Feature {
    override val name: String = "NotifyMonitoringReport"
    override val requestType: Class<out OcppRequest> = NotifyMonitoringReportRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyMonitoringReportResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyMonitoringReportRequest(
    /** The id of the GetMonitoringRequest that requested this report. */
    val requestId: Int,
    /** Sequence number of this message. First message starts at 0. */
    val seqNo: Int,
    /** Timestamp of the moment this message was generated at the Charging Station. */
    val generatedAt: ZonedDateTime,
    val monitor: List<MonitoringData>? = null,
    /** “to be continued” indicator. Indicates whether another part of the monitoringData follows in an upcoming notifyMonitoringReportRequest message. Default value when omitted is false. */
    val tbc: Boolean? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyMonitoringReportResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
