package com.monta.ocpp.emulator.interceptor.model

/**
 * Port the engine uses to ask a front door (today the Compose desktop UI, later potentially an
 * HTTP/MCP server) to let a human edit an intercepted OCPP message before it is sent.
 *
 * The engine must not depend on the Compose UI, so [Interception.Edit] talks to this interface and
 * the app binds its own implementation (the edit-message window view model) in the app Koin module.
 */
interface MessageEditPrompt {
    /**
     * Presents [message] for editing and suspends until an edited value is supplied or
     * [timeoutSeconds] elapses, in which case the original [message] is returned unchanged.
     */
    suspend fun edit(
        message: String,
        timeoutSeconds: Int,
    ): String
}
