package com.monta.ocpp.emulator.control.service

import com.monta.ocpp.emulator.control.model.ControlRequest
import com.monta.ocpp.emulator.control.model.ControlResponse
import com.monta.ocpp.emulator.platform.util.MontaSerialization
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import javax.inject.Singleton

/**
 * A loopback-only TCP control channel for driving a running emulator instance from
 * external test code (see docs/cli/cli-plan.md). Speaks newline-delimited JSON:
 * one [ControlRequest] per line in, one [ControlResponse] per line out.
 */
@Singleton
class ControlServerService(
    private val controlCommandDispatcher: ControlCommandDispatcher,
) {

    companion object {
        private const val DEFAULT_PORT = 9911
        private const val PORT_ENV_VAR = "OCPP_EMULATOR_CONTROL_PORT"
    }

    private val logger = KotlinLogging.logger {}
    private val objectMapper = MontaSerialization.objectMapper

    @Volatile
    private var serverSocket: ServerSocket? = null

    fun start() {
        if (serverSocket != null) {
            return
        }

        val port = System.getenv(PORT_ENV_VAR)?.toIntOrNull() ?: DEFAULT_PORT

        val socket = ServerSocket()
        socket.reuseAddress = true
        socket.bind(InetSocketAddress(InetAddress.getLoopbackAddress(), port))
        serverSocket = socket

        logger.info { "control server listening on ${socket.inetAddress.hostAddress}:${socket.localPort}" }

        Thread {
            acceptLoop(socket)
        }.apply {
            isDaemon = true
            name = "control-server-accept"
        }.start()
    }

    fun stop() {
        val socket = serverSocket ?: return
        serverSocket = null
        try {
            socket.close()
        } catch (exception: IOException) {
            logger.warn(exception) { "failed to close control server socket" }
        }
    }

    private fun acceptLoop(
        socket: ServerSocket,
    ) {
        while (!socket.isClosed) {
            val client = try {
                socket.accept()
            } catch (exception: IOException) {
                if (socket.isClosed) {
                    break
                }
                logger.warn(exception) { "failed to accept control connection" }
                continue
            }

            Thread {
                handleClient(client)
            }.apply {
                isDaemon = true
                name = "control-server-client-${client.port}"
            }.start()
        }
    }

    private fun handleClient(
        client: Socket,
    ) {
        client.use {
            val reader = client.getInputStream().bufferedReader(StandardCharsets.UTF_8)
            val writer = client.getOutputStream().bufferedWriter(StandardCharsets.UTF_8)

            while (true) {
                val line = try {
                    reader.readLine()
                } catch (exception: IOException) {
                    break
                } ?: break

                if (line.isBlank()) {
                    continue
                }

                val response = handleLine(line)

                writer.write(objectMapper.writeValueAsString(response))
                writer.newLine()
                writer.flush()
            }
        }
    }

    private fun handleLine(
        line: String,
    ): ControlResponse {
        // Parse to a raw tree first so a caller-supplied `id` can still be echoed back even
        // when the request fails to bind to ControlRequest (e.g. a missing `command` field) -
        // see docs/cli/cli-plan.md §7: `id` is echoed back unchanged so a client can match
        // responses to pipelined requests, including failure responses.
        val tree = try {
            objectMapper.readTree(line)
        } catch (exception: Exception) {
            return ControlResponse.failure(
                id = null,
                code = "INVALID_REQUEST",
                message = exception.message ?: "malformed request",
            )
        }

        val id = tree.get("id")?.takeUnless { it.isNull }?.asText()

        val request = try {
            objectMapper.treeToValue(tree, ControlRequest::class.java)
        } catch (exception: Exception) {
            return ControlResponse.failure(
                id = id,
                code = "INVALID_REQUEST",
                message = exception.message ?: "malformed request",
            )
        }

        return runBlocking {
            controlCommandDispatcher.dispatch(request)
        }
    }
}
