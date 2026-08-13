// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearMonitoringResult
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object ClearVariableMonitoringFeature : Feature {
    override val name: String = "ClearVariableMonitoring"
    override val requestType: Class<out OcppRequest> = ClearVariableMonitoringRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ClearVariableMonitoringResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearVariableMonitoringRequest(
    /** List of the monitors to be cleared, identified by there Id. */
    val id: List<Int>,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearVariableMonitoringResponse(
    val clearMonitoringResult: List<ClearMonitoringResult>,
    val customData: CustomData? = null,
) : OcppConfirmation
