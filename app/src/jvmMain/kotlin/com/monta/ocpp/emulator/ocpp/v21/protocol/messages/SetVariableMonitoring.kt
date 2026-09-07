// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SetMonitoringData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SetMonitoringResult

object SetVariableMonitoringFeature : Feature {
    override val name: String = "SetVariableMonitoring"
    override val requestType: Class<out OcppRequest> = SetVariableMonitoringRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = SetVariableMonitoringResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetVariableMonitoringRequest(
    val setMonitoringData: List<SetMonitoringData>,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetVariableMonitoringResponse(
    val setMonitoringResult: List<SetMonitoringResult>,
    val customData: CustomData? = null,
) : OcppConfirmation
