package com.monta.ocpp.emulator.chargepoint.connector.exception

class ChargePointConnectorNotFoundException(
    chargePointId: Long,
    connectorPosition: Int,
) : Exception("No connector at position $connectorPosition for charge point $chargePointId")
