// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdToken
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object RequestBatterySwapFeature : Feature {
    override val name: String = "RequestBatterySwap"
    override val requestType: Class<out OcppRequest> = RequestBatterySwapRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = RequestBatterySwapResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class RequestBatterySwapRequest(
    val idToken: IdToken,
    /** Request id to match with BatterySwapRequest. */
    val requestId: Int,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class RequestBatterySwapResponse(
    val status: GenericStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
