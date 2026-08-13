// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericDeviceModelStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ReportBaseEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetBaseReportFeature : Feature {
    override val name: String = "GetBaseReport"
    override val requestType: Class<out OcppRequest> = GetBaseReportRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetBaseReportResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetBaseReportRequest(
    /** The Id of the request. */
    val requestId: Int,
    val reportBase: ReportBaseEnum,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetBaseReportResponse(
    val status: GenericDeviceModelStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
