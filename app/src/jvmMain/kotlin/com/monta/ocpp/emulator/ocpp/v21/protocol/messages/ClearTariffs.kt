// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearTariffsResult
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object ClearTariffsFeature : Feature {
    override val name: String = "ClearTariffs"
    override val requestType: Class<out OcppRequest> = ClearTariffsRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ClearTariffsResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearTariffsRequest(
    /** List of tariff Ids to clear. When absent clears all tariffs at _evseId_. */
    val tariffIds: List<String>? = null,
    /** When present only clear tariffs matching _tariffIds_ at EVSE _evseId_. */
    val evseId: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ClearTariffsResponse(
    val clearTariffsResult: List<ClearTariffsResult>,
    val customData: CustomData? = null,
) : OcppConfirmation
