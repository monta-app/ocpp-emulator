// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class BatteryData(
    /** Slot number where battery is inserted or removed. */
    val evseId: Int,
    /** Serial number of battery. */
    val serialNumber: String,
    /** State of charge */
    val soC: Double,
    /** State of health */
    val soH: Double,
    /** Production date of battery. */
    val productionDate: ZonedDateTime? = null,
    /** Vendor-specific info from battery in undefined format. */
    val vendorInfo: String? = null,
    val customData: CustomData? = null,
)
