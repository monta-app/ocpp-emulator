package com.monta.ocpp.emulator.interceptor.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.monta.ocpp.emulator.interceptor.model.MessageEditPrompt
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class EditMessageWindowViewModel : MessageEditPrompt {
    var channel by mutableStateOf<Channel<String>?>(null)
    var message by mutableStateOf("")

    override suspend fun edit(
        message: String,
        timeoutSeconds: Int,
    ): String {
        val channel = Channel<String>()
        this.channel = channel
        this.message = message
        return try {
            withTimeout(timeoutSeconds.seconds) {
                channel.receive()
            }
        } catch (exception: TimeoutCancellationException) {
            this.channel = null
            this.message = ""
            message
        }
    }
}
