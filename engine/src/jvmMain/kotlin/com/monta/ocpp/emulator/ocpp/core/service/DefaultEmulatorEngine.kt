package com.monta.ocpp.emulator.ocpp.core.service

import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.connector.exception.ChargePointConnectorNotFoundException
import com.monta.ocpp.emulator.chargepoint.connector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v16.extension.stopActiveTransactions
import javax.inject.Singleton

/**
 * Default [EmulatorEngine] that delegates to the existing engine components. Registered by classpath
 * scanning via the `@Singleton` annotation, so [com.monta.ocpp.emulator.EngineKoinModule]'s
 * `@ComponentScan` picks it up and auto-binds it to [EmulatorEngine] — mirroring how the other
 * engine services are wired.
 */
@Singleton
class DefaultEmulatorEngine(
    private val connectionManager: ConnectionManager,
    private val chargePointConnectorService: ChargePointConnectorService,
) : EmulatorEngine {

    override fun connect(
        chargePointId: Long,
    ) {
        connectionManager.connect(chargePointId)
    }

    override fun disconnect(
        chargePointId: Long,
    ) {
        connectionManager.disconnect(chargePointId)
    }

    override suspend fun disconnectAll() {
        connectionManager.disconnectAll()
    }

    override suspend fun stopTransaction(
        chargePointId: Long,
        connectorPosition: Int,
        reason: Reason,
        endReasonDescription: String?,
    ) {
        val connector = chargePointConnectorService.get(
            chargePointId = chargePointId,
            connectorId = connectorPosition,
        ) ?: throw ChargePointConnectorNotFoundException(
            chargePointId = chargePointId,
            connectorPosition = connectorPosition,
        )

        connector.stopActiveTransactions(
            reason = reason,
            endReasonDescription = endReasonDescription,
        )
    }
}
