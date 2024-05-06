package com.monta.ocpp.emulator.chargepoint.model

enum class OcppVersion(val version: String) {
    V16("OCPP-1.6"),
    V201("OCPP-2.0.1");

    override fun toString(): String = this.version
    fun versionNumber(): String = this.version.substringAfter('-')
}
