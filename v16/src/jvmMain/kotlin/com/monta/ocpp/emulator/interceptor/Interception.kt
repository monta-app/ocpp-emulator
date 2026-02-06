package com.monta.ocpp.emulator.interceptor

import com.monta.library.ocpp.common.serialization.Message
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.interceptor.view.EditMessageWindowViewModel
import com.monta.ocpp.emulator.logger.ChargePointLogger
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

sealed class Interception {
    data object NoOp : Interception() {
        override suspend fun intercept(
            message: Message,
        ): String {
            return message.toJsonString(MessageInterceptor.serializer)
        }
    }

    class Block(
        val chargePointId: Long,
    ) : Interception() {
        override suspend fun intercept(
            message: Message,
        ): String? {
            val logMessage = when (message) {
                is Message.Request -> "Blocked request: ${message.action}"
                is Message.Response -> "Blocked response: ${message.uniqueId}"
                else -> throw IllegalStateException("Should not be able to block error message")
            }
            ChargePointLogger.getLogger(chargePointId).warn(message = logMessage)
            return null
        }
    }

    class Delay(
        val chargePointId: Long,
        private val delaySeconds: Int,
    ) : Interception() {
        override suspend fun intercept(
            message: Message,
        ): String {
            val type = if (message is Message.Request) message.action else "message"
            ChargePointLogger.getLogger(chargePointId).warn(message = "Delaying $type for $delaySeconds seconds")
            delay(delaySeconds * 1000L)
            return message.toJsonString(MessageInterceptor.serializer)
        }
    }

    class Edit(
        private val timeoutSeconds: Int,
    ) : Interception() {
        private val channel = Channel<String>()

        override suspend fun intercept(
            message: Message,
        ): String {
            val editMessageWindowViewModel: EditMessageWindowViewModel by injectAnywhere()
            val original = message.toJsonString(MessageInterceptor.serializer)
            editMessageWindowViewModel.channel = channel
            editMessageWindowViewModel.message = original
            return try {
                withTimeout(timeoutSeconds.seconds) {
                    channel.receive()
                }
            } catch (e: TimeoutCancellationException) {
                editMessageWindowViewModel.channel = null
                editMessageWindowViewModel.message = ""
                original
            }
        }
    }

    data object Reject : Interception() {
        override suspend fun intercept(
            message: Message,
        ): String? {
            TODO("send reject back to source, may need to diff between client and server")
        }
    }

    abstract suspend fun intercept(
        message: Message,
    ): String?
}
