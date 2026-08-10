// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.BootReasonEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChargingStation
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.RegistrationStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import java.time.ZonedDateTime

object BootNotificationFeature : Feature {
    override val name: String = "BootNotification"
    override val requestType: Class<out OcppRequest> = BootNotificationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = BootNotificationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class BootNotificationRequest(
    val chargingStation: ChargingStation,
    val reason: BootReasonEnum,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class BootNotificationResponse(
    /** This contains the CSMS’s current time. */
    val currentTime: ZonedDateTime,
    /** When &lt;&lt;cmn_registrationstatusenumtype,Status&gt;&gt; is Accepted, this contains the heartbeat interval in seconds. If the CSMS returns something other than Accepted, the value of the interval field indicates the minimum wait time before sending a next BootNotification request. */
    val interval: Int,
    val status: RegistrationStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
