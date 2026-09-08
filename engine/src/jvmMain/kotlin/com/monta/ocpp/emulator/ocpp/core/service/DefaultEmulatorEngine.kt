package com.monta.ocpp.emulator.ocpp.core.service

import com.monta.library.ocpp.common.serialization.Message
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.connector.exception.ChargePointConnectorNotFoundException
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.chargepoint.connector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.model.SecurityEvent
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.core.service.PreviousMessagesService
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointConnectorDto
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointDto
import com.monta.ocpp.emulator.ocpp.core.model.ChargePointListItemDto
import com.monta.ocpp.emulator.ocpp.core.model.PreviousMessageDto
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v16.extension.setConnectorCarState
import com.monta.ocpp.emulator.ocpp.v16.extension.setMaxVehicleRate
import com.monta.ocpp.emulator.ocpp.v16.extension.setNumberPhases
import com.monta.ocpp.emulator.ocpp.v16.extension.setStatus
import com.monta.ocpp.emulator.ocpp.v16.extension.stopActiveTransactions
import com.monta.ocpp.emulator.ocpp.v16.service.ChargePointManager
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

/**
 * Default [EmulatorEngine] that delegates to the existing engine components and projects DAOs to
 * DTOs. Registered by classpath scanning via `@Singleton`, so [com.monta.ocpp.emulator.EngineKoinModule]'s
 * `@ComponentScan` auto-binds it to [EmulatorEngine] — mirroring how the other engine services are
 * wired.
 *
 * [OcppClientV16] is pulled with [injectAnywhere] rather than constructor-injected because it is
 * built by a Koin `@Single` builder function, and the Koin compile-safety check (KOIN-D001)
 * misanalyses builder-constructed dependencies when they appear as constructor parameters — the same
 * reason [ChargePointManager] pulls it this way.
 */
@Singleton
class DefaultEmulatorEngine(
    private val connectionManager: ConnectionManager,
    private val chargePointService: ChargePointService,
    private val chargePointConnectorService: ChargePointConnectorService,
    private val chargePointRepository: ChargePointRepository,
    private val chargePointManager: ChargePointManager,
    private val previousMessagesService: PreviousMessagesService,
) : EmulatorEngine {

    private val ocppClientV16: OcppClientV16 by injectAnywhere()

    // region Queries

    override fun observeChargePoints(): Flow<List<ChargePointListItemDto>> {
        return chargePointRepository.getAllFlow().map { chargePoints ->
            chargePoints.map { chargePoint -> chargePoint.toListItemDto() }
        }
    }

    override fun observeChargePoint(
        chargePointId: Long,
    ): Flow<ChargePointDto> {
        return chargePointRepository.getByIdFlow(chargePointId).map { chargePoint ->
            chargePoint.toDto()
        }
    }

    override fun observeConnector(
        connectorId: Long,
    ): Flow<ChargePointConnectorDto> {
        return chargePointConnectorService.getByIdFlow(connectorId).map { connector ->
            connector.toDto()
        }
    }

    override fun getChargePoint(
        chargePointId: Long,
    ): ChargePointDto {
        return chargePointService.getById(chargePointId).toDto()
    }

    override fun findChargePoint(
        chargePointId: Long,
    ): ChargePointDto? {
        return chargePointService.findById(chargePointId)?.toDto()
    }

    override fun getPreviousMessages(
        messageType: String,
    ): List<PreviousMessageDto> {
        return previousMessagesService.getAllOfMessageType(messageType)
            .map { previousMessage -> previousMessage.toDto() }
    }

    override fun getConnectedChargePointIds(): List<Long> {
        return chargePointService.getConnectedChargePointIds()
    }

    override fun isChargePointIdentityInUse(
        identity: String,
    ): Boolean {
        return chargePointService.isIdentityInUse(identity)
    }

    override fun normalizeChargePointIdentity(
        identity: String,
    ): String {
        return chargePointService.normalizeIdentity(identity)
    }

    // endregion

    // region Connection lifecycle

    override fun connect(
        chargePointId: Long,
    ) {
        connectionManager.connect(chargePointId)
    }

    override fun disconnect(
        chargePointId: Long,
    ) {
        connectionManager.disconnect(chargePointId)
    }

    override suspend fun disconnectAll() {
        connectionManager.disconnectAll()
    }

    // endregion

    // region Charge-point commands

    override fun upsertChargePoint(
        name: String,
        identity: String,
        password: String?,
        ocppUrl: String,
        apiUrl: String,
        firmware: String,
        maxKw: Double,
        connectorCount: Int,
        meterType: MeterType,
    ): Long {
        return chargePointService.upsert(
            name = name,
            identity = identity,
            password = password,
            ocppUrl = ocppUrl,
            apiUrl = apiUrl,
            firmware = firmware,
            maxKw = maxKw,
            connectorCount = connectorCount,
            meterType = meterType,
        ).idValue
    }

    override fun deleteChargePoint(
        chargePointId: Long,
    ) {
        connectionManager.disconnect(chargePointId)
        chargePointService.delete(chargePointId)
    }

    override suspend fun setChargePointStatus(
        chargePointId: Long,
        status: ChargePointStatus,
    ) {
        chargePointService.getById(chargePointId).setStatus(
            status = status,
        )
    }

    override suspend fun sendSecurityEvent(
        chargePointId: Long,
        securityEvent: SecurityEvent,
        techInfo: String?,
    ) {
        chargePointManager.securityEvent(
            chargePoint = chargePointService.getById(chargePointId),
            securityEvent = securityEvent,
            techInfo = techInfo,
        )
    }

    // endregion

    // region Connector commands

    override suspend fun authorize(
        connectorId: Long,
        idTag: String,
    ) {
        chargePointManager.authorize(
            connector = requireConnector(connectorId),
            idTag = idTag,
        )
    }

    override suspend fun stopTransaction(
        connectorId: Long,
        reason: Reason,
        endReasonDescription: String?,
    ) {
        requireConnector(connectorId).stopActiveTransactions(
            reason = reason,
            endReasonDescription = endReasonDescription,
        )
    }

    override suspend fun setConnectorCarState(
        connectorId: Long,
        carState: CarState,
    ) {
        requireConnector(connectorId).setConnectorCarState(
            carState = carState,
        )
    }

    override suspend fun setConnectorStatus(
        connectorId: Long,
        status: ChargePointStatus,
        errorCode: ChargePointErrorCode,
        vendorId: String?,
        vendorErrorCode: String?,
        info: String?,
        forceUpdate: Boolean,
    ) {
        requireConnector(connectorId).setStatus(
            status = status,
            errorCode = errorCode,
            vendorId = vendorId,
            vendorErrorCode = vendorErrorCode,
            info = info,
            forceUpdate = forceUpdate,
        )
    }

    override suspend fun setConnectorMaxVehicleRate(
        connectorId: Long,
        amps: Double,
    ) {
        requireConnector(connectorId).setMaxVehicleRate(
            amps = amps,
        )
    }

    override suspend fun setConnectorNumberPhases(
        connectorId: Long,
        numberPhases: Int,
    ) {
        requireConnector(connectorId).setNumberPhases(
            numberPhases = numberPhases,
        )
    }

    // endregion

    // region Raw messaging

    override suspend fun sendRawMessage(
        chargePointId: Long,
        message: Message,
    ) {
        val chargePoint = chargePointService.getById(chargePointId)
        ocppClientV16.sendMessage(
            OcppSession.Info(
                serverId = "",
                identity = chargePoint.identity,
            ),
            message,
        )
    }

    override fun savePreviousMessage(
        messageType: String,
        message: String,
    ) {
        previousMessagesService.insertNewMessage(
            messageType = messageType,
            message = message,
        )
    }

    override fun deletePreviousMessage(
        id: Long,
    ) {
        previousMessagesService.deleteMessage(id)
    }

    // endregion

    private fun requireConnector(
        connectorId: Long,
    ): ChargePointConnectorDAO {
        val connector = chargePointConnectorService.getById(connectorId)
        if (connector == null) {
            throw ChargePointConnectorNotFoundException(connectorId)
        }
        return connector
    }
}
