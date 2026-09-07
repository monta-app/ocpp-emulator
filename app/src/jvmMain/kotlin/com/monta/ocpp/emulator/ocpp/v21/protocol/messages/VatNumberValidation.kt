// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Address
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object VatNumberValidationFeature : Feature {
    override val name: String = "VatNumberValidation"
    override val requestType: Class<out OcppRequest> = VatNumberValidationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = VatNumberValidationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class VatNumberValidationRequest(
    /** VAT number to check. */
    val vatNumber: String,
    /** EVSE id for which check is done */
    val evseId: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class VatNumberValidationResponse(
    /** VAT number that was requested. */
    val vatNumber: String,
    val status: GenericStatusEnum,
    val company: Address? = null,
    val statusInfo: StatusInfo? = null,
    /** EVSE id for which check was requested. */
    val evseId: Int? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
