package com.monta.ocpp.emulator.eichrecht

import com.fasterxml.jackson.annotation.JsonProperty

data class OCMFSignature(
    /**
     * Signature Algorithm: Selects the algorithm used to create the signature.
     * This includes the signature algorithm, its parameters, and the hash algorithm that will be applied to the data to be signed.
     * This specification is optional. If it is omitted, the default value is effective.
     */
    @JsonProperty("SA")
    val signatureAlgorithm: String?,

    /**
     * Signature Encoding: Indicates how the signature data is encoded to be stored in the JSON string.
     * This specification is optional. If it is omitted, the default value is effective.
     * The following values are possible:
     * - hex – The signature data is represented in the JSON string in hexadecimal encoding (default)
     * - base64 – The signature data is base64 encoded in the JSON string.
     */
    @JsonProperty("SE")
    val signatureEncoding: String? = null,

    /**
     * Signature Mime Type: Indicates how the signature data is to be interpreted.
     * This specification is optional. If it is omitted, the default value is effective.
     * The following values are possible:
     * - `application/x-der` – DER encoded ASN.1 structure (default)
     */
    @JsonProperty("SM")
    val signatureMimeType: String? = null,

    /**
     * Signature Data: The actual signature data according to the format specification above.
     */
    @JsonProperty("SD")
    val signatureData: String
)
