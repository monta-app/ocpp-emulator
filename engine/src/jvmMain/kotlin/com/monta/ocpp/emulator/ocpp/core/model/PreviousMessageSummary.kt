package com.monta.ocpp.emulator.ocpp.core.model

import kotlinx.serialization.Serializable

/**
 * Read-only projection of a stored raw-message template
 * ([com.monta.ocpp.emulator.chargepoint.core.entity.PreviousMessagesDAO]) for the Send Message
 * window's history list. DAO-free so the window can list and replay templates without Exposed.
 */
@Serializable
data class PreviousMessageSummary(
    val id: Long,
    val messageType: String,
    val message: String,
)
