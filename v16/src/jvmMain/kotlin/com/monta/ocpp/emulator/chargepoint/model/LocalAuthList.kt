package com.monta.ocpp.emulator.chargepoint.model

data class LocalAuthList(
    var version: Int = 1,
    var tokens: MutableSet<String> = mutableSetOf()
)
