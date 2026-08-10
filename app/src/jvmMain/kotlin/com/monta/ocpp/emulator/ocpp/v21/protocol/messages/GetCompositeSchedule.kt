// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingRateUnitEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CompositeSchedule
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object GetCompositeScheduleFeature : Feature {
    override val name: String = "GetCompositeSchedule"
    override val requestType: Class<out OcppRequest> = GetCompositeScheduleRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = GetCompositeScheduleResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetCompositeScheduleRequest(
    /** Length of the requested schedule in seconds. */
    val duration: Int,
    /** The ID of the EVSE for which the schedule is requested. When evseid=0, the Charging Station will calculate the expected consumption for the grid connection. */
    val evseId: Int,
    val chargingRateUnit: ChargingRateUnitEnum? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class GetCompositeScheduleResponse(
    val status: GenericStatusEnum,
    val statusInfo: StatusInfo? = null,
    val schedule: CompositeSchedule? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
