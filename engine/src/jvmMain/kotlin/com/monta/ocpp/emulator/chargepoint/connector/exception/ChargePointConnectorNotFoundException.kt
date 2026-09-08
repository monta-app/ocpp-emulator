package com.monta.ocpp.emulator.chargepoint.connector.exception

class ChargePointConnectorNotFoundException(
    connectorId: Long,
) : Exception("No connector with id $connectorId")
