package com.monta.ocpp.emulator.ocpp.core.service

import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.connector.exception.ChargePointConnectorNotFoundException
import com.monta.ocpp.emulator.chargepoint.connector.repository.ChargePointConnectorRepository
import com.monta.ocpp.emulator.chargepoint.connector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.core.service.PreviousMessagesService
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v16.service.ChargePointManager
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Exercises the DB-backed half of [DefaultEmulatorEngine] against a throwaway SQLite database.
 *
 * `connect`/`disconnect`/`disconnectAll` are not covered here — they open real ktor websockets to a
 * CSMS, which this harness deliberately does not stand up. `stopTransaction` *is* reachable: the
 * OCPP `StopTransaction` send it triggers is wrapped in a swallowing `try/catch` in the engine, so
 * with no live session the message send is a no-op while the persisted transaction is still closed
 * out — which is exactly the reason/description plumbing this test asserts on.
 */
class DefaultEmulatorEngineTest : DatabaseSpec({

    val chargePointRepository = ChargePointRepository()
    val chargePointService = ChargePointService(chargePointRepository)

    val engine = DefaultEmulatorEngine(
        connectionManager = ConnectionManager(
            messageInterceptor = MessageInterceptor(chargePointService),
            chargePointRepository = chargePointRepository,
        ),
        chargePointService = chargePointService,
        chargePointConnectorService = ChargePointConnectorService(ChargePointConnectorRepository()),
        chargePointRepository = chargePointRepository,
        chargePointManager = ChargePointManager(),
        previousMessagesService = PreviousMessagesService(),
    )

    fun seedChargePoint(
        identity: String = "MEM_001",
        connectorCount: Int = 1,
    ): ChargePointDAO {
        return chargePointService.upsert(
            name = "Emulator",
            identity = identity,
            password = null,
            ocppUrl = "wss://example.invalid/ocpp",
            apiUrl = "https://example.invalid",
            firmware = "1.0.0",
            maxKw = 22.0,
            connectorCount = connectorCount,
            meterType = MeterType.OCPP,
        )
    }

    describe("stopTransaction") {

        it("throws the connector-not-found exception when the connector cannot be resolved") {
            shouldThrow<ChargePointConnectorNotFoundException> {
                engine.stopTransaction(connectorId = 999_999)
            }
        }

        it("forwards the given reason and description through to the stopped transaction") {
            val chargePoint = seedChargePoint(connectorCount = 1)

            val (connectorId, transactionId) = transaction {
                val connector = chargePoint.connectors.first { connector -> connector.position == 1 }
                val activeTransaction = ChargePointTransactionDAO.newInstance(
                    chargePoint = chargePoint,
                    chargePointConnector = connector,
                    externalId = 1234,
                    idTag = "TAG",
                )
                connector.activeTransaction = activeTransaction
                connector.idValue to activeTransaction.idValue
            }

            engine.stopTransaction(
                connectorId = connectorId,
                reason = Reason.EVDisconnected,
                endReasonDescription = "Stopped by user",
            )

            transaction {
                val stopped = ChargePointTransactionDAO.findById(transactionId).shouldNotBeNull()

                // The reviewer's bug hardcoded Reason.Local and dropped the description; assert the
                // caller's values survived instead of a hardcoded default.
                stopped.endReason shouldBe Reason.EVDisconnected
                stopped.endReasonDescription shouldBe "Stopped by user"
                stopped.endTime.shouldNotBeNull()
            }
        }
    }
})
