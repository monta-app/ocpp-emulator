package com.monta.ocpp.emulator.chargepoint.core.model

enum class MeterType(val displayName: String) {
    OCPP("OCPP"),
    Eichrecht("Eichrecht"),
    OcppHighPrecision("OCPP high precision"),
    ;

    override fun toString(): String = displayName
}
