// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DataTransferStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object DataTransferFeature : Feature {
    override val name: String = "DataTransfer"
    override val requestType: Class<out OcppRequest> = DataTransferRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = DataTransferResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class DataTransferRequest(
    /** This identifies the Vendor specific implementation */
    val vendorId: String,
    /** May be used to indicate a specific message or implementation. */
    val messageId: String? = null,
    /** Data without specified length or format. This needs to be decided by both parties (Open to implementation). */
    val `data`: Any? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class DataTransferResponse(
    val status: DataTransferStatusEnum,
    val statusInfo: StatusInfo? = null,
    /** Data without specified length or format, in response to request. */
    val `data`: Any? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
