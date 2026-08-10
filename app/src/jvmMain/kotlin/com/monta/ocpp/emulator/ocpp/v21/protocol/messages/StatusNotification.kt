// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ConnectorStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import java.time.ZonedDateTime

object StatusNotificationFeature : Feature {
    override val name: String = "StatusNotification"
    override val requestType: Class<out OcppRequest> = StatusNotificationRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = StatusNotificationResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class StatusNotificationRequest(
    /** The time for which the status is reported. */
    val timestamp: ZonedDateTime,
    val connectorStatus: ConnectorStatusEnum,
    /** The id of the EVSE to which the connector belongs for which the the status is reported. */
    val evseId: Int,
    /** The id of the connector within the EVSE for which the status is reported. */
    val connectorId: Int,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class StatusNotificationResponse(
    val customData: CustomData? = null,
) : OcppConfirmation
