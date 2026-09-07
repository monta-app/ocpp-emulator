// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingSchedule
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import java.time.ZonedDateTime

object NotifyEVChargingScheduleFeature : Feature {
    override val name: String = "NotifyEVChargingSchedule"
    override val requestType: Class<out OcppRequest> = NotifyEVChargingScheduleRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyEVChargingScheduleResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyEVChargingScheduleRequest(
    /** Periods contained in the charging profile are relative to this point in time. */
    val timeBase: ZonedDateTime,
    val chargingSchedule: ChargingSchedule,
    /** The charging schedule contained in this notification applies to an EVSE. EvseId must be &gt; 0. */
    val evseId: Int,
    /** *(2.1)* Id of the _chargingSchedule_ that EV selected from the provided ChargingProfile. */
    val selectedChargingScheduleId: Int? = null,
    /** *(2.1)* True when power tolerance is accepted by EV. This value is taken from EVPowerProfile.PowerToleranceAcceptance in the ISO 15118-20 PowerDeliverReq message.. */
    val powerToleranceAcceptance: Boolean? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyEVChargingScheduleResponse(
    val status: GenericStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
