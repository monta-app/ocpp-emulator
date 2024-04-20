package com.monta.ocpp.emulator.logger

interface Loggable {
    fun chargePointId(): Long
    fun connectorPosition(): Int
}
