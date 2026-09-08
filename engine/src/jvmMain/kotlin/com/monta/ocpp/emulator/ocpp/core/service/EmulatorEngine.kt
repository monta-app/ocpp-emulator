package com.monta.ocpp.emulator.ocpp.core.service

import com.monta.library.ocpp.common.serialization.Message
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.model.SecurityEvent
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointConnectorSummary
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointListItem
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointSummary
import com.monta.ocpp.emulator.ocpp.core.model.PreviousMessageSummary
import kotlinx.coroutines.flow.Flow

/**
 * Headless entry point for driving the emulator.
 *
 * This is the module boundary for `:engine`: every read the UI needs is a [Flow] of a plain DTO
 * ([ChargePointSummary], [ChargePointConnectorSummary], …) and every mutation is a command
 * addressing rows by their numeric id. No Exposed DAO and no OCPP-protocol machinery
 * (`ConnectionManager`, the DAO extensions) crosses this interface — callers (the Compose UI today,
 * tests, any future headless driver) never touch the persistence or protocol layers directly.
 *
 * The facade holds no logic of its own: every member delegates to an existing engine
 * service/repository or maps a DAO to its DTO.
 *
 * Rows are addressed the same way throughout: charge points by `chargePointId`, connectors by
 * `connectorId`. Callers always hold the DTO they are acting on, and every DTO carries its own `id`,
 * so no command has to re-resolve a row from a composite (charge point, position) key.
 *
 * The one deliberate OCPP-library leak is [sendRawMessage], whose [Message] argument is the raw
 * protocol envelope the Send Message window builds by hand — that window bypasses the emulator state
 * machine on purpose, so there is no higher-level command to model it with.
 */
interface EmulatorEngine {

    // region Queries — observable, DTO-projected reads

    /**
     * Cold flow of every charge point as a lightweight [ChargePointListItem], re-emitted whenever any
     * charge point row changes. Carries no connectors — use [observeChargePoint] when those are
     * needed, so listing never pays for the connector and transaction traversal.
     */
    fun observeChargePoints(): Flow<List<ChargePointListItem>>

    /** Cold flow of a single charge point (and its connectors), re-emitted on any change to it. */
    fun observeChargePoint(
        chargePointId: Long,
    ): Flow<ChargePointSummary>

    /** Cold flow of a single connector, re-emitted whenever that connector row changes. */
    fun observeConnector(
        connectorId: Long,
    ): Flow<ChargePointConnectorSummary>

    /** Point-in-time snapshot of a charge point. Throws if it cannot be resolved. */
    fun getChargePoint(
        chargePointId: Long,
    ): ChargePointSummary

    /** Point-in-time snapshot of a charge point, or `null` if no such charge point exists. */
    fun findChargePoint(
        chargePointId: Long,
    ): ChargePointSummary?

    /** The stored raw-message templates for a given OCPP action, newest first. */
    fun getPreviousMessages(
        messageType: String,
    ): List<PreviousMessageSummary>

    /** The ids of every charge point currently connected. */
    fun getConnectedChargePointIds(): List<Long>

    /** Whether a charge point already exists with the given (normalised) identity. */
    fun isChargePointIdentityInUse(
        identity: String,
    ): Boolean

    /** Normalises an identity to its stored form (trimmed, upper-cased). */
    fun normalizeChargePointIdentity(
        identity: String,
    ): String

    // endregion

    // region Connection lifecycle

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

    // endregion

    // region Charge-point commands

    /**
     * Creates a charge point or updates the existing one matching [identity], reconciling its
     * connector rows to [connectorCount]. Returns the charge point's id.
     */
    fun upsertChargePoint(
        name: String,
        identity: String,
        password: String?,
        ocppUrl: String,
        apiUrl: String,
        firmware: String,
        maxKw: Double,
        connectorCount: Int,
        meterType: MeterType,
    ): Long

    /** Disconnects then permanently removes a charge point with its connectors and transactions. */
    fun deleteChargePoint(
        chargePointId: Long,
    )

    /** Sets the charge point's own status (connector 0) and pushes a StatusNotification. */
    suspend fun setChargePointStatus(
        chargePointId: Long,
        status: ChargePointStatus,
    )

    /** Sends a SecurityEventNotification for the charge point. */
    suspend fun sendSecurityEvent(
        chargePointId: Long,
        securityEvent: SecurityEvent,
        techInfo: String?,
    )

    // endregion

    // region Connector commands

    /** Presents an RFID id tag on a connector, starting a transaction if the CSMS accepts it. */
    suspend fun authorize(
        connectorId: Long,
        idTag: String,
    )

    /**
     * Stops every active transaction on the given connector, forwarding [reason] and
     * [endReasonDescription] verbatim so the CSMS and the persisted transaction record the caller's
     * intent rather than a hardcoded default.
     */
    suspend fun stopTransaction(
        connectorId: Long,
        reason: Reason = Reason.Local,
        endReasonDescription: String? = null,
    )

    /** Sets the connector's car state (A/B/C) and recalculates the resulting connector status. */
    suspend fun setConnectorCarState(
        connectorId: Long,
        carState: CarState,
    )

    /** Pushes an explicit StatusNotification for a connector. */
    suspend fun setConnectorStatus(
        connectorId: Long,
        status: ChargePointStatus,
        errorCode: ChargePointErrorCode = ChargePointErrorCode.NoError,
        vendorId: String? = null,
        vendorErrorCode: String? = null,
        info: String? = null,
        forceUpdate: Boolean = false,
    )

    /** Sets the connector's simulated vehicle max amps per phase and recalculates status. */
    suspend fun setConnectorMaxVehicleRate(
        connectorId: Long,
        amps: Double,
    )

    /** Sets the connector's simulated vehicle phase count. */
    suspend fun setConnectorNumberPhases(
        connectorId: Long,
        numberPhases: Int,
    )

    // endregion

    // region Raw messaging

    /**
     * Sends a pre-built raw OCPP [Message] on the charge point's session, bypassing the emulator
     * state machine. Used by the Send Message window; the [Message] type is the one OCPP-library
     * type this facade intentionally exposes (see the type KDoc).
     */
    suspend fun sendRawMessage(
        chargePointId: Long,
        message: Message,
    )

    /** Persists a raw-message template so the Send Message window can replay it later. */
    fun savePreviousMessage(
        messageType: String,
        message: String,
    )

    /** Deletes a stored raw-message template by id. */
    fun deletePreviousMessage(
        id: Long,
    )

    // endregion
}
