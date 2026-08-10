// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChangeAvailabilityStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.EVSE
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.OperationalStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object ChangeAvailabilityFeature : Feature {
    override val name: String = "ChangeAvailability"
    override val requestType: Class<out OcppRequest> = ChangeAvailabilityRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ChangeAvailabilityResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ChangeAvailabilityRequest(
    val operationalStatus: OperationalStatusEnum,
    val evse: EVSE? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ChangeAvailabilityResponse(
    val status: ChangeAvailabilityStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
