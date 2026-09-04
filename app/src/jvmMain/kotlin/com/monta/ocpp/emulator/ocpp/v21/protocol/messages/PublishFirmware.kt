// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages

import com.monta.library.ocpp.common.profile.Feature
import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.StatusInfo

object PublishFirmwareFeature : Feature {
    override val name: String = "PublishFirmware"
    override val requestType: Class<out OcppRequest> = PublishFirmwareRequest::class.java
    override val confirmationType: Class<out OcppConfirmation> = PublishFirmwareResponse::class.java
}

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class PublishFirmwareRequest(
    /** This contains a string containing a URI pointing to a location from which to retrieve the firmware. */
    val location: String,
    /** The MD5 checksum over the entire firmware file as a hexadecimal string of length 32. */
    val checksum: String,
    /** The Id of the request. */
    val requestId: Int,
    /** This specifies how many times Charging Station must retry to download the firmware before giving up. If this field is not present, it is left to Charging Station to decide how many times it wants to retry. If the value is 0, it means: no retries. */
    val retries: Int? = null,
    /** The interval in seconds after which a retry may be attempted. If this field is not present, it is left to Charging Station to decide how long to wait between attempts. */
    val retryInterval: Int? = null,
    val customData: CustomData? = null,
) : OcppRequest

/** OCPP 2.1 Edition 1 (c) OCA, Creative Commons Attribution-NoDerivatives 4.0 International Public License */
data class PublishFirmwareResponse(
    val status: GenericStatusEnum,
    val statusInfo: StatusInfo? = null,
    val customData: CustomData? = null,
) : OcppConfirmation
