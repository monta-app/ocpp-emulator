// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.UnlockStatusEnum

object UnlockConnectorFeature : Feature {
    override val name: String = "UnlockConnector"
    override val requestType: Class<out OcppRequest> = UnlockConnectorRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = UnlockConnectorResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class UnlockConnectorRequest(
    /** This contains the identifier of the EVSE for which a connector needs to be unlocked. */
    val evseId: Int,
    /** This contains the identifier of the connector that needs to be unlocked. */
    val connectorId: Int,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class UnlockConnectorResponse(
    val status: UnlockStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
