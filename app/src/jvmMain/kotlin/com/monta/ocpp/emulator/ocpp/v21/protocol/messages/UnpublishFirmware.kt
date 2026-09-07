// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.UnpublishFirmwareStatusEnum

object UnpublishFirmwareFeature : Feature {
    override val name: String = "UnpublishFirmware"
    override val requestType: Class<out OcppRequest> = UnpublishFirmwareRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = UnpublishFirmwareResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class UnpublishFirmwareRequest(
    /** The MD5 checksum over the entire firmware file as a hexadecimal string of length 32. */
    val checksum: String,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class UnpublishFirmwareResponse(
    val status: UnpublishFirmwareStatusEnum,
    val customData: CustomData? = null,
) : OcppConfirmation
