package com.monta.ocpp.emulator.chargepoint.core.model

enum class OcppVersion(val version: String, val subprotocol: String) {
    V16("OCPP-1.6", "ocpp1.6"),
    V201("OCPP-2.0.1", "ocpp2.0.1"),
    V21("OCPP-2.1", "ocpp2.1"),
    ;

    override fun toString(): String = this.version
    fun versionNumber(): String = this.version.substringAfter('-')
}
