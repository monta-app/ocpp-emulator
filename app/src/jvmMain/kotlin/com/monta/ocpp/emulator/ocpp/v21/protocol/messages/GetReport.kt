// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ComponentCriterionEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ComponentVariable
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericDeviceModelStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetReportFeature : Feature {
    override val name: String = "GetReport"
    override val requestType: Class<out OcppRequest> = GetReportRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetReportResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetReportRequest(
    /** The Id of the request. */
    val requestId: Int,
    val componentVariable: List<ComponentVariable>? = null,
    /** This field contains criteria for components for which a report is requested */
    val componentCriteria: List<ComponentCriterionEnum>? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetReportResponse(
    val status: GenericDeviceModelStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
