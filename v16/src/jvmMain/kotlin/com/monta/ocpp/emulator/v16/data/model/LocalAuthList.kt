package com.monta.ocpp.emulator.v16.data.model

data class LocalAuthList(
    var version: Int = 1,
    var tokens: MutableSet<String> = mutableSetOf()
)
