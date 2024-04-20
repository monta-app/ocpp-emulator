package com.monta.ocpp.emulator.logger

@Suppress("unused")
object GlobalLogger {

    suspend fun error(
        loggable: Loggable,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        loggable.getLogger().error(
            connectorId = loggable.connectorPosition(),
            message = message,
            context = context
        )
    }

    suspend fun warn(
        loggable: Loggable,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        loggable.getLogger().warn(
            connectorId = loggable.connectorPosition(),
            message = message,
            context = context
        )
    }

    suspend fun info(
        loggable: Loggable,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        loggable.getLogger().info(
            connectorId = loggable.connectorPosition(),
            message = message,
            context = context
        )
    }

    suspend fun debug(
        loggable: Loggable,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        loggable.getLogger().debug(
            connectorId = loggable.connectorPosition(),
            message = message,
            context = context
        )
    }

    suspend fun trace(
        loggable: Loggable,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        loggable.getLogger().trace(
            connectorId = loggable.connectorPosition(),
            message = message,
            context = context
        )
    }

    suspend fun logSend(
        loggable: Loggable,
        message: String
    ) {
        loggable.getLogger().logSend(message)
    }

    suspend fun logReceive(
        loggable: Loggable,
        message: String
    ) {
        loggable.getLogger().logReceive(message)
    }

    // Util

    private fun Loggable.getLogger(): ChargePointLogger {
        return ChargePointLogger.getLogger(chargePointId())
    }
}
