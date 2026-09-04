// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.Tariff
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TariffChangeStatusEnum

object ChangeTransactionTariffFeature : Feature {
    override val name: String = "ChangeTransactionTariff"
    override val requestType: Class<out OcppRequest> = ChangeTransactionTariffRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = ChangeTransactionTariffResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ChangeTransactionTariffRequest(
    val tariff: Tariff,
    /** Transaction id for new tariff. */
    val transactionId: String,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class ChangeTransactionTariffResponse(
    val status: TariffChangeStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
