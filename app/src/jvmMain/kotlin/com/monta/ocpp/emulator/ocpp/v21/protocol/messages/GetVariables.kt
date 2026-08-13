// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetVariableData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetVariableResult

object GetVariablesFeature : Feature {
    override val name: String = "GetVariables"
    override val requestType: Class<out OcppRequest> = GetVariablesRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetVariablesResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetVariablesRequest(
    val getVariableData: List<GetVariableData>,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetVariablesResponse(
    val getVariableResult: List<GetVariableResult>,
    val customData: CustomData? = null,
) : OcppConfirmation
