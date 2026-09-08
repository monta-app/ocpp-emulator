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
    // Capacity-1 so `trySend` from the composable never suspends or drops while the channel is empty
    // (there is at most one pending edit at a time).
    private var channel by mutableStateOf<Channel<String>?>(null)
    var message by mutableStateOf("")

    /** Whether an edit prompt is currently open — drives whether the window renders. */
    val isEditing: Boolean
        get() = channel != null

    override suspend fun edit(
        message: String,
        timeoutSeconds: Int,
    ): String {
        val channel = Channel<String>(capacity = 1)
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

    /** Completes the open edit prompt with [edited] and resets the window state. */
    fun submit(
        edited: String,
    ) {
        channel?.trySend(edited)
        message = ""
        channel = null
    }
}
