// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingLimit
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingSchedule
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData

object NotifyChargingLimitFeature : Feature {
    override val name: String = "NotifyChargingLimit"
    override val requestType: Class<out OcppRequest> = NotifyChargingLimitRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyChargingLimitResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyChargingLimitRequest(
    val chargingLimit: ChargingLimit,
    val chargingSchedule: List<ChargingSchedule>? = null,
    /** The EVSE to which the charging limit is set. If absent or when zero, it applies to the entire Charging Station. */
    val evseId: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyChargingLimitResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
