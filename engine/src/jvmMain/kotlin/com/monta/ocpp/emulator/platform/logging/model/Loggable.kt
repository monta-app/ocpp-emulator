package com.monta.ocpp.emulator.platform.logging.model

interface Loggable {
    fun chargePointId(): Long
    fun connectorPosition(): Int
}
