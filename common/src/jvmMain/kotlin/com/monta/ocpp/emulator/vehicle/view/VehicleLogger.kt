package com.monta.ocpp.emulator.vehicle.view

import com.monta.ocpp.emulator.common.util.PrettyJsonFormatter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import java.time.OffsetDateTime

@Singleton
class VehicleLogger {

    private val logger = KotlinLogging.logger {}

    val logFlow = MutableSharedFlow<LogEntry>(
        replay = 20,
        extraBufferCapacity = 20,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    enum class Level {
        Error,
        Warn,
        Info,
        Debug,
        Trace
    }

    data class LogEntry(
        val level: Level,
        val message: String,
        val context: Map<String, Any?>? = null,
        val timestamp: OffsetDateTime = OffsetDateTime.now()
    )

    suspend fun error(
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.error(message)
        log(Level.Error, message, context)
    }

    suspend fun warn(
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.warn(message)
        log(Level.Warn, message, context)
    }

    suspend fun info(
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.info(message)
        log(Level.Info, message, context)
    }

    suspend fun debug(
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.debug(message)
        log(Level.Debug, message, context)
    }

    suspend fun trace(
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.trace(message)
        log(Level.Trace, message, context)
    }

    suspend fun logSend(
        message: String
    ) {
        trace(
            message = buildString {
                appendLine(
                    PrettyJsonFormatter.formatJson(message)
                )
            },
            context = mapOf(
                "json" to message
            )
        )
    }

    suspend fun logReceive(
        message: String
    ) {
        trace(
            message = buildString {
                appendLine(
                    PrettyJsonFormatter.formatJson(message)
                )
            },
            context = mapOf(
                "json" to message
            )
        )
    }

    private suspend fun log(
        level: Level,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logFlow.emit(
            value = LogEntry(
                level = level,
                message = message,
                context = context
            )
        )
    }
}
