// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.LogEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.LogParameters
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.LogStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetLogFeature : Feature {
    override val name: String = "GetLog"
    override val requestType: Class<out OcppRequest> = GetLogRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetLogResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetLogRequest(
    val log: LogParameters,
    val logType: LogEnum,
    /** The Id of this request */
    val requestId: Int,
    /** This specifies how many times the Charging Station must retry to upload the log before giving up. If this field is not present, it is left to Charging Station to decide how many times it wants to retry. If the value is 0, it means: no retries. */
    val retries: Int? = null,
    /** The interval in seconds after which a retry may be attempted. If this field is not present, it is left to Charging Station to decide how long to wait between attempts. */
    val retryInterval: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetLogResponse(
    val status: LogStatusEnum,
    val statusInfo: StatusInfo? = null,
    /** This contains the name of the log file that will be uploaded. This field is not present when no logging information is available. */
    val filename: String? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
