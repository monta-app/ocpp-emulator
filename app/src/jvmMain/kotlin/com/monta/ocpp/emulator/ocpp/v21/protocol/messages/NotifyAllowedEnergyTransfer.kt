// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.EnergyTransferModeEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.NotifyAllowedEnergyTransferStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object NotifyAllowedEnergyTransferFeature : Feature {
    override val name: String = "NotifyAllowedEnergyTransfer"
    override val requestType: Class<out OcppRequest> = NotifyAllowedEnergyTransferRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = NotifyAllowedEnergyTransferResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyAllowedEnergyTransferRequest(
    /** The transaction for which the allowed energy transfer is allowed. */
    val transactionId: String,
    /** Modes of energy transfer that are accepted by CSMS. */
    val allowedEnergyTransfer: List<EnergyTransferModeEnum>,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class NotifyAllowedEnergyTransferResponse(
    val status: NotifyAllowedEnergyTransferStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
