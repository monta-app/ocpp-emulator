// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class OCSPRequestData(
    val hashAlgorithm: HashAlgorithmEnum,
    /** The hash of the issuer’s distinguished name (DN), that must be calculated over the DER encoding of the issuer’s name field in the certificate being checked. */
    val issuerNameHash: String,
    /** The hash of the DER encoded public key: the value (excluding tag and length) of the subject public key field in the issuer’s certificate. */
    val issuerKeyHash: String,
    /** The string representation of the hexadecimal value of the serial number without the prefix "0x" and without leading zeroes. */
    val serialNumber: String,
    /** This contains the responder URL (Case insensitive). */
    val responderURL: String,
    val customData: CustomData? = null,
)
