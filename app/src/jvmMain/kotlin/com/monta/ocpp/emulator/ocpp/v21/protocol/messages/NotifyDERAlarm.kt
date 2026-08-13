// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DERControlEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GridEventFaultEnum
import java.time.ZonedDateTime

object NotifyDERAlarmFeature : Feature {
    override val name: String = "NotifyDERAlarm"
    override val requestType: Class<out OcppRequest> = NotifyDERAlarmRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyDERAlarmResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyDERAlarmRequest(
    val controlType: DERControlEnum,
    /** Time of start or end of alarm. */
    val timestamp: ZonedDateTime,
    val gridEventFault: GridEventFaultEnum? = null,
    /** True when error condition has ended. Absent or false when alarm has started. */
    val alarmEnded: Boolean? = null,
    /** Optional info provided by EV. */
    val extraInfo: String? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyDERAlarmResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
