package com.monta.ocpp.emulator.logger

import com.monta.ocpp.emulator.common.util.PrettyJsonFormatter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import mu.KotlinLogging
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

class ChargePointLogger private constructor() {

    companion object {

        private val loggers = ConcurrentHashMap<Long, ChargePointLogger>()

        @JvmStatic
        fun getLogger(
            chargePointId: Long
        ): ChargePointLogger {
            return loggers.getOrPut(chargePointId) {
                ChargePointLogger()
            }
        }
    }

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
        connectorId: Int = 0,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.error("[$connectorId] $message")
        log(Level.Error, message, context, connectorId)
    }

    suspend fun warn(
        connectorId: Int = 0,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.warn("[$connectorId] $message")
        log(Level.Warn, message, context, connectorId)
    }

    suspend fun info(
        connectorId: Int = 0,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.info("[$connectorId] $message")
        log(Level.Info, message, context, connectorId)
    }

    suspend fun debug(
        connectorId: Int = 0,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.debug("[$connectorId] $message")
        log(Level.Debug, message, context, connectorId)
    }

    suspend fun trace(
        connectorId: Int = 0,
        message: String,
        context: Map<String, Any?>? = null
    ) {
        logger.trace("[$connectorId] $message")
        log(Level.Trace, message, context, connectorId)
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
        context: Map<String, Any?>? = null,
        connectorId: Int
    ) {
        logFlow.emit(
            value = LogEntry(
                level = level,
                message = "[$connectorId] $message",
                context = context
            )
        )
    }
}
