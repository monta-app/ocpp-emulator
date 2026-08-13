// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.BatteryData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.BatterySwapEventEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdToken

object BatterySwapFeature : Feature {
    override val name: String = "BatterySwap"
    override val requestType: Class<out OcppRequest> = BatterySwapRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = BatterySwapResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class BatterySwapRequest(
    val batteryData: List<BatteryData>,
    val eventType: BatterySwapEventEnum,
    val idToken: IdToken,
    /** RequestId to correlate BatteryIn/Out events and optional RequestBatterySwapRequest. */
    val requestId: Int,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class BatterySwapResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
