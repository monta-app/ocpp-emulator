// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingNeeds
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.NotifyEVChargingNeedsStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import java.time.ZonedDateTime

object NotifyEVChargingNeedsFeature : Feature {
    override val name: String = "NotifyEVChargingNeeds"
    override val requestType: Class<out OcppRequest> = NotifyEVChargingNeedsRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyEVChargingNeedsResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyEVChargingNeedsRequest(
    /** Defines the EVSE and connector to which the EV is connected. EvseId may not be 0. */
    val evseId: Int,
    val chargingNeeds: ChargingNeeds,
    /** Contains the maximum elements the EV supports for: + - ISO 15118-2: schedule tuples in SASchedule (both Pmax and Tariff). + - ISO 15118-20: PowerScheduleEntry, PriceRule and PriceLevelScheduleEntries. */
    val maxScheduleTuples: Int? = null,
    /** *(2.1)* Time when EV charging needs were received. + Field can be added when charging station was offline when charging needs were received. */
    val timestamp: ZonedDateTime? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyEVChargingNeedsResponse(
    val status: NotifyEVChargingNeedsStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
