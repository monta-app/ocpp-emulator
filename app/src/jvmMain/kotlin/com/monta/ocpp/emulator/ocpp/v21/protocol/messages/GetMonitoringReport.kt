// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ComponentVariable
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericDeviceModelStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.MonitoringCriterionEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetMonitoringReportFeature : Feature {
    override val name: String = "GetMonitoringReport"
    override val requestType: Class<out OcppRequest> = GetMonitoringReportRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetMonitoringReportResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetMonitoringReportRequest(
    /** The Id of the request. */
    val requestId: Int,
    val componentVariable: List<ComponentVariable>? = null,
    /** This field contains criteria for components for which a monitoring report is requested */
    val monitoringCriteria: List<MonitoringCriterionEnum>? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetMonitoringReportResponse(
    val status: GenericDeviceModelStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
