package com.monta.ocpp.emulator.control.service

import com.fasterxml.jackson.databind.JsonNode
import com.monta.ocpp.emulator.platform.util.MontaSerialization
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * End-to-end test of [ControlServerService]'s NDJSON socket protocol over a real loopback
 * [Socket] (not just the dispatcher directly), covering the framing/id-matching behavior
 * documented in remotecontrol/README.md. Uses the real [ControlTestFixture.dispatcher] and a real
 * loopback [Socket], bound to an OS-assigned ephemeral port (`port = 0`) via
 * [ControlServerService.start] so this doesn't collide with a real running emulator instance
 * (or other test runs) on the default port.
 */
class ControlServerServiceTest {

    private val objectMapper = MontaSerialization.objectMapper

    private lateinit var service: ControlServerService
    private lateinit var socket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    @BeforeEach
    fun startServerAndConnect() {
        service = ControlServerService(ControlTestFixture.dispatcher)
        service.start(port = 0)

        val port = service.boundPort ?: error("control server did not bind a port")
        socket = Socket("127.0.0.1", port)
        reader = socket.getInputStream().bufferedReader(StandardCharsets.UTF_8)
        writer = socket.getOutputStream().bufferedWriter(StandardCharsets.UTF_8)
    }

    @AfterEach
    fun disconnectAndStopServer() {
        socket.close()
        service.stop()
    }

    private fun send(
        line: String,
    ): JsonNode {
        writer.write(line)
        writer.newLine()
        writer.flush()

        val responseLine = reader.readLine() ?: error("control server closed the connection without responding")
        return objectMapper.readTree(responseLine)
    }

    @Test
    fun `hello round-trips over the socket as JSON`() {
        val response = send("""{"id":"1","command":"hello","params":{}}""")

        assertEquals("1", response.get("id").asText())
        assertTrue(response.get("ok").asBoolean())
        assertTrue(response.get("result").get("emulatorVersion").isTextual)
    }

    @Test
    fun `completely malformed JSON is rejected without an id`() {
        val response = send("not json at all")

        // id is omitted from the wire response (MontaSerialization excludes nulls), not sent as JSON null.
        assertNull(response.get("id"))
        assertFalse(response.get("ok").asBoolean())
        assertEquals("INVALID_REQUEST", response.get("error").get("code").asText())
    }

    @Test
    fun `well-formed JSON missing 'command' still echoes the caller's id`() {
        // Regression test: a request that parses as JSON but fails to bind to ControlRequest
        // (e.g. a missing required `command` field) used to lose the caller-supplied `id`,
        // breaking the id-matching contract documented in remotecontrol/README.md ("id is
        // caller-supplied and echoed back unchanged, so a test client can pipeline several
        // commands on one connection and match responses to requests").
        val response = send("""{"id":"42","params":{}}""")

        assertEquals("42", response.get("id").asText())
        assertFalse(response.get("ok").asBoolean())
        assertEquals("INVALID_REQUEST", response.get("error").get("code").asText())
    }

    @Test
    fun `unknown command echoes id and reports UNKNOWN_COMMAND`() {
        val response = send("""{"id":"2","command":"bogus.command","params":{}}""")

        assertEquals("2", response.get("id").asText())
        assertFalse(response.get("ok").asBoolean())
        assertEquals("UNKNOWN_COMMAND", response.get("error").get("code").asText())
    }

    @Test
    fun `a request without an id gets a response without an id`() {
        val response = send("""{"command":"hello","params":{}}""")

        assertNull(response.get("id"))
        assertTrue(response.get("ok").asBoolean())
    }

    @Test
    fun `blank lines between commands are ignored, connection stays usable`() {
        writer.write("")
        writer.newLine()
        writer.flush()

        val response = send("""{"id":"3","command":"hello","params":{}}""")

        assertEquals("3", response.get("id").asText())
        assertTrue(response.get("ok").asBoolean())
    }

    @Test
    fun `one connection can send multiple commands sequentially and match responses by id`() {
        val first = send("""{"id":"a","command":"hello","params":{}}""")
        val second = send("""{"id":"b","command":"bogus.command","params":{}}""")
        val third = send("""{"id":"c","command":"hello","params":{}}""")

        assertEquals("a", first.get("id").asText())
        assertTrue(first.get("ok").asBoolean())

        assertEquals("b", second.get("id").asText())
        assertFalse(second.get("ok").asBoolean())

        assertEquals("c", third.get("id").asText())
        assertTrue(third.get("ok").asBoolean())
    }
}
