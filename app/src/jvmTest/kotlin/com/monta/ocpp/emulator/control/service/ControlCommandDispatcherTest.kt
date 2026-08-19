package com.monta.ocpp.emulator.control.service

import com.fasterxml.jackson.databind.JsonNode
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.control.model.ChargePointStateResult
import com.monta.ocpp.emulator.control.model.ConnectorIdentityParams
import com.monta.ocpp.emulator.control.model.ConnectorStateResult
import com.monta.ocpp.emulator.control.model.ControlRequest
import com.monta.ocpp.emulator.control.model.HelloResult
import com.monta.ocpp.emulator.control.model.IdentityParams
import com.monta.ocpp.emulator.control.model.SetAvailabilityParams
import com.monta.ocpp.emulator.control.model.SetCarStateParams
import com.monta.ocpp.emulator.control.model.SetConnectorStatusParams
import com.monta.ocpp.emulator.control.model.StopTransactionParams
import com.monta.ocpp.emulator.platform.util.MontaSerialization
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Exercises [ControlCommandDispatcher] against the real (SQLite-backed) service graph in
 * [ControlTestFixture] - command routing, params binding, and the error-code mapping in
 * [ControlCommandDispatcher.dispatch]. See [ControlTestFixture]'s doc comment for what's
 * deliberately out of scope (`connect`/`disconnect`/`authorize`, which need a live OCPP
 * websocket).
 */
class ControlCommandDispatcherTest {

    private val dispatcher = ControlTestFixture.dispatcher
    private val objectMapper = MontaSerialization.objectMapper

    private fun paramsOf(
        value: Any,
    ): JsonNode = objectMapper.valueToTree(value)

    @Test
    fun `hello returns the emulator version and echoes id`() = runBlocking {
        val response = dispatcher.dispatch(ControlRequest(command = "hello", id = "1"))

        assertEquals("1", response.id)
        assertTrue(response.ok)
        assertTrue(response.result is HelloResult)
    }

    @Test
    fun `unknown command is rejected with UNKNOWN_COMMAND`() = runBlocking {
        val response = dispatcher.dispatch(ControlRequest(command = "bogus.command", id = "2"))

        assertEquals("2", response.id)
        assertFalse(response.ok)
        assertEquals("UNKNOWN_COMMAND", response.error?.code)
    }

    @Test
    fun `params missing a required field are rejected with INVALID_PARAMS`() = runBlocking {
        // chargePoint.getState requires `identity`; this only has `connectorId`.
        val params = objectMapper.readTree("""{"connectorId": 1}""")

        val response = dispatcher.dispatch(
            ControlRequest(command = "chargePoint.getState", id = "3", params = params),
        )

        assertEquals("3", response.id)
        assertFalse(response.ok)
        assertEquals("INVALID_PARAMS", response.error?.code)
    }

    @Test
    fun `getState for an unknown identity is rejected with CHARGE_POINT_NOT_FOUND`() = runBlocking {
        val response = dispatcher.dispatch(
            ControlRequest(
                command = "chargePoint.getState",
                id = "4",
                params = paramsOf(IdentityParams(identity = "CP-DOES-NOT-EXIST")),
            ),
        )

        assertFalse(response.ok)
        assertEquals("CHARGE_POINT_NOT_FOUND", response.error?.code)
    }

    @Test
    fun `getState for a known identity returns persisted state`() = runBlocking {
        val identity = "CP-DISPATCH-GETSTATE"
        ControlTestFixture.seedChargePoint(identity)

        val response = dispatcher.dispatch(
            ControlRequest(
                command = "chargePoint.getState",
                id = "5",
                params = paramsOf(IdentityParams(identity = identity)),
            ),
        )

        assertTrue(response.ok)
        val result = response.result as ChargePointStateResult
        assertEquals(identity, result.identity)
        assertFalse(result.connected)
        assertEquals(ChargePointStatus.Unavailable, result.status)
        assertEquals(ChargePointErrorCode.NoError, result.errorCode)
        assertTrue(result.connectors.isEmpty())
    }

    @Test
    fun `connector getState self-heals a missing connector with defaults`() = runBlocking {
        val identity = "CP-DISPATCH-CONNSTATE"
        ControlTestFixture.seedChargePoint(identity)

        val response = dispatcher.dispatch(
            ControlRequest(
                command = "connector.getState",
                id = "6",
                params = paramsOf(ConnectorIdentityParams(identity = identity, connectorId = 1)),
            ),
        )

        assertTrue(response.ok)
        val result = response.result as ConnectorStateResult
        assertEquals(1, result.connectorId)
        assertEquals(ChargePointStatus.Available, result.status)
        assertEquals(ChargePointErrorCode.NoError, result.errorCode)
        assertEquals(CarState.C, result.carState)
        assertNull(result.activeTransactionId)
        assertFalse(result.locked)
    }

    @Test
    fun `setAvailability persists the new charge point status`() = runBlocking {
        val identity = "CP-DISPATCH-AVAIL"
        ControlTestFixture.seedChargePoint(identity)

        val response = dispatcher.dispatch(
            ControlRequest(
                command = "chargePoint.setAvailability",
                id = "7",
                params = paramsOf(SetAvailabilityParams(identity = identity, status = ChargePointStatus.Available)),
            ),
        )

        assertTrue(response.ok)
        assertEquals(ChargePointStatus.Available, (response.result as ChargePointStateResult).status)

        // Re-fetch independently to confirm the write was actually persisted, not just echoed back.
        val getStateResponse = dispatcher.dispatch(
            ControlRequest(
                command = "chargePoint.getState",
                id = "7b",
                params = paramsOf(IdentityParams(identity = identity)),
            ),
        )
        assertEquals(ChargePointStatus.Available, (getStateResponse.result as ChargePointStateResult).status)
    }

    @Test
    fun `setCarState persists the new car state`() = runBlocking {
        val identity = "CP-DISPATCH-CARSTATE"
        ControlTestFixture.seedChargePoint(identity)

        val response = dispatcher.dispatch(
            ControlRequest(
                command = "connector.setCarState",
                id = "8",
                params = paramsOf(SetCarStateParams(identity = identity, carState = CarState.A, connectorId = 1)),
            ),
        )

        assertTrue(response.ok)
        assertEquals(CarState.A, (response.result as ConnectorStateResult).carState)
    }

    @Test
    fun `setStatus applies a raw status and error code override`() = runBlocking {
        val identity = "CP-DISPATCH-SETSTATUS"
        ControlTestFixture.seedChargePoint(identity)

        val response = dispatcher.dispatch(
            ControlRequest(
                command = "connector.setStatus",
                id = "9",
                params = paramsOf(
                    SetConnectorStatusParams(
                        identity = identity,
                        status = ChargePointStatus.Faulted,
                        errorCode = ChargePointErrorCode.OverVoltage,
                    ),
                ),
            ),
        )

        assertTrue(response.ok)
        val result = response.result as ConnectorStateResult
        assertEquals(ChargePointStatus.Faulted, result.status)
        assertEquals(ChargePointErrorCode.OverVoltage, result.errorCode)
    }

    @Test
    fun `stopTransaction is a no-op when there is no active transaction`() = runBlocking {
        val identity = "CP-DISPATCH-STOPTX"
        ControlTestFixture.seedChargePoint(identity)

        val response = dispatcher.dispatch(
            ControlRequest(
                command = "connector.stopTransaction",
                id = "10",
                params = paramsOf(StopTransactionParams(identity = identity, connectorId = 1)),
            ),
        )

        assertTrue(response.ok)
        assertNull((response.result as ConnectorStateResult).activeTransactionId)
    }
}
