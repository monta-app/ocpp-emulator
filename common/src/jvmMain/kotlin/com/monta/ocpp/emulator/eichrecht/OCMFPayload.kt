package com.monta.ocpp.emulator.eichrecht

import com.fasterxml.jackson.annotation.JsonProperty

data class OCMFPayload(
    /*
     * General Information
     */

    /**
     * Format Version: Version of the data format in the representation <major>.<minor>.
     * The version specification is coded according to the version of this document,
     * i.e. 0.4 corresponds to major 0 and minor 4.
     * The revision (third digit) is not transmitted, since this does not change anything in the format itself.
     */
    @JsonProperty("FV")
    val formatVersion: String,
    /**
     * Gateway Identification: Identifier of the manufacturer for the system which has generated the present data (manufacturer, model, variant, etc.).
     */
    @JsonProperty("GI")
    val gatewayIdentification: String? = null,
    /**
     * Gateway Serial: Serial number of the above mentioned system. This field is conditionally mandatory.
     */
    @JsonProperty("GS")
    val gatewaySerial: String? = null,
    /**
     * Gateway Version: Version designation of the manufacturer for the software of the above mentioned system. This field is optional.
     */
    @JsonProperty("GV")
    val gatewayVersion: String? = null,

    /**
     * Pagination of the entire data set, i.e. the data that is combined in one signature.
     * Format: `<indicator><number>`
     * The string is composed of an identifying letter for the context and a number without leading zeros.
     * There is a separate independent pagination counter for each context.
     * The following indicators are defined:
     * - `T`: Transaction – readings in transaction reference (mandatory)
     * - `F`: Fiscal – readings independent of transactions (optional)
     *
     * The respective pagination counter is incremented after each use for a record.
     */
    @JsonProperty("PG")
    val pagination: String,

    /*
     * Meter Identification
     */

    /**
     * Meter Vendor: Manufacturer identification of the meter, name of the manufacturer
     */
    @JsonProperty("MV")
    val meterVendor: String? = null,
    /**
     * Meter Model: Model identification of the meter
     */
    @JsonProperty("MM")
    val meterModel: String? = null,
    /**
     * Meter Serial: Serial number of the meter
     */
    @JsonProperty("MS")
    val meterSerial: String,
    /**
     * Meter Firmware: Firmware version of the meter
     */
    @JsonProperty("MF")
    val meterFirmware: String? = null,

    /*
     * User Assignment
     */

    /**
     * Identification Status: General status for user assignment:
     * - true: user successfully assigned
     * - false: user not assigned
     */
    @JsonProperty("IS")
    val identificationStatus: Boolean,
    /**
     * Identification Level: Encoded overall status of the user assignment, represented by an identifier from table 11.
     */
    @JsonProperty("IL")
    val identificationLevel: String? = null,
    /**
     * Identification Flags: Detailed statements about the user assignment, represented by one or more identifiers from table 13 to table 16.
     * The identifiers are always noted as string elements in an array. Also one or no element must be noted as an array.
     */
    @JsonProperty("IF")
    val identificationFlags: List<String>? = null,
    /**
     * Identification Type: Type of identification data, identifier see table 17.
     */
    @JsonProperty("IT")
    val identificationType: String,
    /**
     * Identification Data: The actual identification data according to the type from table 17, e.g. a hex-coded UID according to ISO 14443.
     */
    @JsonProperty("ID")
    val identificationData: String? = null,
    /**
     * Tariff Text: A textual description used to identify a unique tariff.
     * This field is intended for the tariff designation in "Direct Payment" use case.
     */
    @JsonProperty("TT")
    val tariffText: String? = null,

    /*
     * Assignment of the Charge Point
     */

    /**
     * Charge Point Identification Type: Type of the specification for the identification of the charge point, identifier see table 18.
     */
    @JsonProperty("CT")
    val chargePointIdentificationType: String? = null,
    /**
     * Charge Point Identification: Identification information for the charge point.
     */
    @JsonProperty("CI")
    val chargePointIdentification: String? = null,

    /**
     * The list of readings
     */
    @JsonProperty("RD")
    val readings: List<OCMFReading>,
)
