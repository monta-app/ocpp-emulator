// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TariffAssignment
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TariffGetStatusEnum

object GetTariffsFeature : Feature {
    override val name: String = "GetTariffs"
    override val requestType: Class<out OcppRequest> = GetTariffsRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetTariffsResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetTariffsRequest(
    /** EVSE id to get tariff from. When _evseId_ = 0, this gets tariffs from all EVSEs. */
    val evseId: Int,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetTariffsResponse(
    val status: TariffGetStatusEnum,
    val statusInfo: StatusInfo? = null,
    val tariffAssignments: List<TariffAssignment>? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
