package com.monta.ocpp.emulator.testsupport

import com.monta.library.ocpp.common.serialization.Message
import com.monta.library.ocpp.common.serialization.ParsingResult
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor
import tools.jackson.databind.JsonNode
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class FakeCsms(
    private val identity: String,
) {

    data class Sent(
        val uniqueId: String,
        val action: String,
        val payload: JsonNode,
    )

    private val serializer = MessageInterceptor.serializer
    private val overrides = ConcurrentHashMap<String, String>()
    private val nextTransactionId = AtomicInteger(1000)

    val sent = CopyOnWriteArrayList<Sent>()

    lateinit var client: OcppClientV16

    fun respondTo(
        action: String,
        payloadJson: String,
    ) {
        overrides[action] = payloadJson
    }

    suspend fun onFrame(
        rawMessage: String,
    ) {
        val parsed = serializer.parse(rawMessage)
        if (parsed !is ParsingResult.Success) {
            return
        }
        val message = parsed.value
        if (message !is Message.Request) {
            return
        }

        sent.add(Sent(uniqueId = message.uniqueId, action = message.action, payload = message.payload))

        val responsePayload = overrides[message.action] ?: defaultConfirmation(message.action)
        client.receiveMessage(identity, """[3,"${message.uniqueId}",$responsePayload]""")
    }

    suspend fun send(
        action: String,
        payloadJson: String = "{}",
        uniqueId: String = UUID.randomUUID().toString(),
    ) {
        client.receiveMessage(identity, """[2,"$uniqueId","$action",$payloadJson]""")
    }

    fun actions(): List<String> {
        return sent.map { frame -> frame.action }
    }

    fun countOf(
        action: String,
    ): Int {
        return sent.count { frame -> frame.action == action }
    }

    fun lastOf(
        action: String,
    ): Sent? {
        return sent.lastOrNull { frame -> frame.action == action }
    }

    fun allOf(
        action: String,
    ): List<Sent> {
        return sent.filter { frame -> frame.action == action }
    }

    fun clear() {
        sent.clear()
    }

    private fun defaultConfirmation(
        action: String,
    ): String {
        val now = DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now(ZoneOffset.UTC))
        return when (action) {
            "BootNotification" -> """{"status":"Accepted","currentTime":"$now","interval":300}"""
            "Heartbeat" -> """{"currentTime":"$now"}"""
            "Authorize" -> """{"idTagInfo":{"status":"Accepted"}}"""
            "StartTransaction" -> {
                """{"transactionId":${nextTransactionId.getAndIncrement()},"idTagInfo":{"status":"Accepted"}}"""
            }

            "StopTransaction" -> """{"idTagInfo":{"status":"Accepted"}}"""
            "DataTransfer" -> """{"status":"Accepted"}"""
            else -> "{}"
        }
    }
}
