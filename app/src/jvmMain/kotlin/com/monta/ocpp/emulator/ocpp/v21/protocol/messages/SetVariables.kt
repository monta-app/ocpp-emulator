// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SetVariableData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SetVariableResult

object SetVariablesFeature : Feature {
    override val name: String = "SetVariables"
    override val requestType: Class<out OcppRequest> = SetVariablesRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = SetVariablesResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetVariablesRequest(
    val setVariableData: List<SetVariableData>,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class SetVariablesResponse(
    val setVariableResult: List<SetVariableResult>,
    val customData: CustomData? = null,
) : OcppConfirmation
