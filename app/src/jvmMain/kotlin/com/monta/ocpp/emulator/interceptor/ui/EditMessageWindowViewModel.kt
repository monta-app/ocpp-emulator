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
    private var pending by mutableStateOf<Channel<String>?>(null)

    /** The payload being edited, bound two-way to the window's text field. */
    var message by mutableStateOf("")

    /** Whether an edit prompt is currently open — drives whether the window renders. */
    val isEditing: Boolean
        get() = pending != null

    /**
     * [edit] owns the prompt's whole lifecycle: it opens the prompt, and its `finally` closes it on
     * every exit path (confirmed, timed out, or cancelled). [submit] only delivers a value, so there
     * is exactly one place that clears the state.
     */
    override suspend fun edit(
        message: String,
        timeoutSeconds: Int,
    ): String {
        val channel = Channel<String>(capacity = 1)
        pending = channel
        this.message = message
        return try {
            withTimeout(timeoutSeconds.seconds) {
                channel.receive()
            }
        } catch (exception: TimeoutCancellationException) {
            message
        } finally {
            pending = null
            this.message = ""
        }
    }

    /** Completes the open edit prompt with whatever is currently in [message]. */
    fun submit() {
        pending?.trySend(message)
    }
}
