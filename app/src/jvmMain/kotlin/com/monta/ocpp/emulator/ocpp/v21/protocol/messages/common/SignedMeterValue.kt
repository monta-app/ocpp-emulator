// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class SignedMeterValue(
    /** Base64 encoded, contains the signed data from the meter in the format specified in _encodingMethod_, which might contain more then just the meter value. It can contain information like timestamps, reference to a customer etc. */
    val signedMeterData: String,
    /** Format used by the energy meter to encode the meter data. For example: OCMF or EDL. */
    val encodingMethod: String,
    /** *(2.1)* Method used to create the digital signature. Optional, if already included in _signedMeterData_. Standard values for this are defined in Appendix as SigningMethodEnumStringType. */
    val signingMethod: String? = null,
    /** *(2.1)* Base64 encoded, sending depends on configuration variable _PublicKeyWithSignedMeterValue_. */
    val publicKey: String? = null,
    val customData: CustomData? = null,
)
