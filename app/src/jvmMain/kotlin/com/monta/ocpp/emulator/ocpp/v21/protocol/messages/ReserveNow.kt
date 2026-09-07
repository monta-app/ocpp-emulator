// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.IdToken
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ReserveNowStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import java.time.ZonedDateTime

object ReserveNowFeature : Feature {
    override val name: String = "ReserveNow"
    override val requestType: Class<out OcppRequest> = ReserveNowRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ReserveNowResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ReserveNowRequest(
    /** Id of reservation. */
    val id: Int,
    /** Date and time at which the reservation expires. */
    val expiryDateTime: ZonedDateTime,
    val idToken: IdToken,
    /** This field specifies the connector type. Values defined in Appendix as ConnectorEnumStringType. */
    val connectorType: String? = null,
    /** This contains ID of the evse to be reserved. */
    val evseId: Int? = null,
    val groupIdToken: IdToken? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ReserveNowResponse(
    val status: ReserveNowStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
