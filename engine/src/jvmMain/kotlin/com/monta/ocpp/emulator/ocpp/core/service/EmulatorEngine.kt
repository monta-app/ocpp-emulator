package com.monta.ocpp.emulator.ocpp.core.service

import com.monta.library.ocpp.v16.core.Reason

/**
 * Headless entry point for driving the emulator's connection lifecycle.
 *
 * This is a thin command facade over the engine's connection/transaction components so callers
 * (the Compose UI today, tests and any future headless driver) address charge points by their
 * numeric id without reaching into [com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager]
 * or the DAO extensions directly. It holds no logic of its own — every method delegates.
 */
interface EmulatorEngine {

    /** Opens (or re-opens) the websocket connection for the given charge point. */
    fun connect(
        chargePointId: Long,
    )

    /** Tears down the websocket connection for the given charge point. */
    fun disconnect(
        chargePointId: Long,
    )

    /** Tears down every currently-tracked connection and waits for them to finish. */
    suspend fun disconnectAll()

    /**
     * Stops every active transaction on the given connector, forwarding [reason] and
     * [endReasonDescription] verbatim to the stop so the CSMS and the persisted transaction
     * record the caller's intent rather than a hardcoded default.
     */
    suspend fun stopTransaction(
        chargePointId: Long,
        connectorPosition: Int,
        reason: Reason = Reason.Local,
        endReasonDescription: String? = null,
    )
}
