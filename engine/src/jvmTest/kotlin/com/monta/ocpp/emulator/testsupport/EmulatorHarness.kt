package com.monta.ocpp.emulator.testsupport

import com.monta.library.ocpp.common.session.OcppSessionRepository
import com.monta.library.ocpp.common.transport.OcppSettings
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.client.OcppClientV16Builder
import com.monta.ocpp.emulator.chargepoint.connector.repository.ChargePointConnectorRepository
import com.monta.ocpp.emulator.chargepoint.connector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.core.service.PreviousMessagesService
import com.monta.ocpp.emulator.chargepoint.transaction.repository.ChargePointTransactionRepository
import com.monta.ocpp.emulator.chargepoint.transaction.service.ChargePointTransactionService
import com.monta.ocpp.emulator.chargepoint.txdefault.repository.TxDefaultRepository
import com.monta.ocpp.emulator.chargepoint.txdefault.service.TxDefaultService
import com.monta.ocpp.emulator.interceptor.service.MessageInterceptor
import com.monta.ocpp.emulator.ocpp.core.service.DefaultEmulatorEngine
import com.monta.ocpp.emulator.ocpp.core.service.EmulatorEngine
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v16.profile.ChangeConfigurationService
import com.monta.ocpp.emulator.ocpp.v16.profile.CoreClientHandler
import com.monta.ocpp.emulator.ocpp.v16.profile.FirmwareManagementHandler
import com.monta.ocpp.emulator.ocpp.v16.profile.LocalAuthHandler
import com.monta.ocpp.emulator.ocpp.v16.profile.OcppClientEventsHandler
import com.monta.ocpp.emulator.ocpp.v16.profile.SecurityManagementHandler
import com.monta.ocpp.emulator.ocpp.v16.profile.SmartChargingClientHandler
import com.monta.ocpp.emulator.ocpp.v16.profile.TriggerMessageHandler
import com.monta.ocpp.emulator.ocpp.v16.service.ChargePointManager
import com.monta.ocpp.emulator.platform.database.extension.idValue
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.time.Instant

class EmulatorHarness private constructor(
    val engine: EmulatorEngine,
    val csms: FakeCsms,
    val client: OcppClientV16,
    val chargePointManager: ChargePointManager,
    val chargePointService: ChargePointService,
    val identity: String,
    val chargePointId: Long,
    val connectorIds: List<Long>,
) {

    companion object {

        suspend fun start(
            identity: String = "MEM_001",
            connectorCount: Int = 1,
            maxKw: Double = 22.0,
            meterType: MeterType = MeterType.OCPP,
            booted: Boolean = true,
        ): EmulatorHarness {
            val csms = FakeCsms(identity)
            val services = EngineServices.create()
            val client = buildOcppClient(services, csms)
            csms.client = client
            val emulatorEngine = buildEmulatorEngine(services)

            registerGraph(services, client, emulatorEngine)

            val chargePoint = seedChargePoint(services, identity, connectorCount, maxKw, meterType)
            val chargePointId = transaction { chargePoint.idValue }
            val connectorIds = transaction {
                chargePoint.connectors.sortedBy { connector -> connector.position }
                    .map { connector -> connector.idValue }
            }

            markConnectedAndSuppressAutoBoot(chargePoint)
            client.connect(
                identity = identity,
                isReconnecting = false,
                sendFrame = { message -> csms.onFrame(message) },
                closeConnection = { },
            )
            if (!booted) {
                resetToUnbooted(chargePoint, csms)
            }

            return EmulatorHarness(
                engine = emulatorEngine,
                csms = csms,
                client = client,
                chargePointManager = services.chargePointManager,
                chargePointService = services.chargePointService,
                identity = identity,
                chargePointId = chargePointId,
                connectorIds = connectorIds,
            )
        }

        private fun buildOcppClient(
            services: EngineServices,
            csms: FakeCsms,
        ): OcppClientV16 {
            val firmwareManagementHandler = FirmwareManagementHandler()
            return OcppClientV16Builder()
                .settings(OcppSettings(nanoSecondDates = false))
                .onConnect { ocppSessionInfo, reconnecting ->
                    OcppClientEventsHandler().onConnect(ocppSessionInfo, reconnecting)
                }
                .onDisconnect { ocppSessionInfo ->
                    OcppClientEventsHandler().onDisconnect(ocppSessionInfo)
                }
                .addSendHook { chargePointIdentity, message ->
                    services.messageInterceptor.intercept(chargePointIdentity, message)
                }
                .localMode(OcppSessionRepository())
                .addCore(CoreClientHandler())
                .addTriggerMessage(TriggerMessageHandler())
                .addLocalAuth(LocalAuthHandler())
                .addSmartCharge(
                    SmartChargingClientHandler(
                        services.chargePointService,
                        services.chargePointTransactionService,
                        services.txDefaultService,
                    ),
                )
                .addFirmwareManagement(firmwareManagementHandler)
                .addSecurity(SecurityManagementHandler(firmwareManagementHandler))
                .build()
        }

        private fun buildEmulatorEngine(
            services: EngineServices,
        ): EmulatorEngine {
            return DefaultEmulatorEngine(
                connectionManager = services.connectionManager,
                chargePointService = services.chargePointService,
                chargePointConnectorService = services.chargePointConnectorService,
                chargePointRepository = services.chargePointRepository,
                chargePointManager = services.chargePointManager,
                previousMessagesService = services.previousMessagesService,
            )
        }

        private fun registerGraph(
            services: EngineServices,
            client: OcppClientV16,
            emulatorEngine: EmulatorEngine,
        ) {
            startKoin {
                modules(
                    module {
                        single { client }
                        single { services.chargePointRepository }
                        single { services.chargePointService }
                        single { services.chargePointConnectorService }
                        single { services.chargePointTransactionService }
                        single { services.txDefaultService }
                        single { services.previousMessagesService }
                        single { services.messageInterceptor }
                        single { services.connectionManager }
                        single { services.chargePointManager }
                        single { ChangeConfigurationService() }
                        single { emulatorEngine }
                    },
                )
            }
        }

        private fun seedChargePoint(
            services: EngineServices,
            identity: String,
            connectorCount: Int,
            maxKw: Double,
            meterType: MeterType,
        ): ChargePointDAO {
            return services.chargePointService.upsert(
                name = "Emulator",
                identity = identity,
                password = null,
                ocppUrl = "wss://example.invalid/ocpp",
                apiUrl = "https://example.invalid",
                firmware = "1.0.0",
                maxKw = maxKw,
                connectorCount = connectorCount,
                meterType = meterType,
            )
        }

        /** `onConnect` starts a boot sequence on a background thread whenever `bootedAt` is null. */
        private fun markConnectedAndSuppressAutoBoot(
            chargePoint: ChargePointDAO,
        ) {
            transaction {
                chargePoint.connected = true
                chargePoint.bootedAt = Instant.now()
            }
        }

        private fun resetToUnbooted(
            chargePoint: ChargePointDAO,
            csms: FakeCsms,
        ) {
            transaction {
                chargePoint.bootedAt = null
            }
            csms.clear()
        }
    }

    val connectorId: Long
        get() {
            return connectorIds.first()
        }

    fun chargePoint(): ChargePointDAO {
        return chargePointService.getById(chargePointId)
    }
}

private class EngineServices(
    val chargePointRepository: ChargePointRepository,
    val chargePointService: ChargePointService,
    val chargePointConnectorService: ChargePointConnectorService,
    val chargePointTransactionService: ChargePointTransactionService,
    val txDefaultService: TxDefaultService,
    val previousMessagesService: PreviousMessagesService,
    val messageInterceptor: MessageInterceptor,
    val connectionManager: ConnectionManager,
    val chargePointManager: ChargePointManager,
) {
    companion object {
        fun create(): EngineServices {
            val chargePointRepository = ChargePointRepository()
            val chargePointService = ChargePointService(chargePointRepository)
            val messageInterceptor = MessageInterceptor(chargePointService)
            return EngineServices(
                chargePointRepository = chargePointRepository,
                chargePointService = chargePointService,
                chargePointConnectorService = ChargePointConnectorService(ChargePointConnectorRepository()),
                chargePointTransactionService = ChargePointTransactionService(ChargePointTransactionRepository()),
                txDefaultService = TxDefaultService(TxDefaultRepository()),
                previousMessagesService = PreviousMessagesService(),
                messageInterceptor = messageInterceptor,
                connectionManager = ConnectionManager(messageInterceptor, chargePointRepository),
                chargePointManager = ChargePointManager(),
            )
        }
    }
}
