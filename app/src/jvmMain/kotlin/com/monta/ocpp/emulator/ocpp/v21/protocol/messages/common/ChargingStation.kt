// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ChargingStation(
    /** Defines the model of the device. */
    val model: String,
    /** Identifies the vendor (not necessarily in a unique manner). */
    val vendorName: String,
    /** Vendor-specific device identifier. */
    val serialNumber: String? = null,
    val modem: Modem? = null,
    /** This contains the firmware version of the Charging Station. */
    val firmwareVersion: String? = null,
    val customData: CustomData? = null,
)
