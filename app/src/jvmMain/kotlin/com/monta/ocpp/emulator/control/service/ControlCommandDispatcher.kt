package com.monta.ocpp.emulator.control.service

import com.fasterxml.jackson.databind.JsonNode
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.exception.ChargePointNotFoundException
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.control.exception.ControlCommandException
import com.monta.ocpp.emulator.control.model.AuthorizeParams
import com.monta.ocpp.emulator.control.model.AuthorizeResult
import com.monta.ocpp.emulator.control.model.ChargePointStateResult
import com.monta.ocpp.emulator.control.model.ConnectorIdentityParams
import com.monta.ocpp.emulator.control.model.ConnectorStateResult
import com.monta.ocpp.emulator.control.model.ControlRequest
import com.monta.ocpp.emulator.control.model.ControlResponse
import com.monta.ocpp.emulator.control.model.HelloResult
import com.monta.ocpp.emulator.control.model.IdentityParams
import com.monta.ocpp.emulator.control.model.SetAvailabilityParams
import com.monta.ocpp.emulator.control.model.SetCarStateParams
import com.monta.ocpp.emulator.control.model.SetConnectorStatusParams
import com.monta.ocpp.emulator.control.model.StopTransactionParams
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v16.extension.setConnectorCarState
import com.monta.ocpp.emulator.ocpp.v16.extension.setStatus
import com.monta.ocpp.emulator.ocpp.v16.extension.stopActiveTransactions
import com.monta.ocpp.emulator.ocpp.v16.service.ChargePointManager
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.MontaSerialization
import io.github.oshai.kotlinlogging.KotlinLogging
import javax.inject.Singleton

@Singleton
class ControlCommandDispatcher(
    private val chargePointService: ChargePointService,
    private val connectionManager: ConnectionManager,
    private val chargePointManager: ChargePointManager,
) {

    private val logger = KotlinLogging.logger {}
    private val objectMapper = MontaSerialization.objectMapper

    suspend fun dispatch(
        request: ControlRequest,
    ): ControlResponse {
        return try {
            val result = handle(request.command, request.params)
            ControlResponse.success(request.id, result)
        } catch (exception: ControlCommandException) {
            ControlResponse.failure(request.id, exception.code, exception.message ?: exception.code)
        } catch (exception: ChargePointNotFoundException) {
            ControlResponse.failure(
                id = request.id,
                code = "CHARGE_POINT_NOT_FOUND",
                message = exception.message ?: "charge point not found",
            )
        } catch (exception: Exception) {
            logger.warn(exception) { "control command failed command=${request.command}" }
            ControlResponse.failure(
                id = request.id,
                code = "INTERNAL_ERROR",
                message = exception.message ?: "internal error",
            )
        }
    }

    private suspend fun handle(
        command: String,
        params: JsonNode,
    ): Any? {
        return when (command) {
            "hello" -> hello()
            "chargePoint.connect" -> connect(params)
            "chargePoint.disconnect" -> disconnect(params)
            "chargePoint.setAvailability" -> setAvailability(params)
            "chargePoint.getState" -> getChargePointState(params)
            "connector.setCarState" -> setCarState(params)
            "connector.setStatus" -> setConnectorStatus(params)
            "connector.authorize" -> authorize(params)
            "connector.stopTransaction" -> stopTransaction(params)
            "connector.getState" -> getConnectorState(params)
            else -> throw ControlCommandException("UNKNOWN_COMMAND", "unknown command '$command'")
        }
    }

    private fun hello(): HelloResult {
        return HelloResult(
            emulatorVersion = System.getProperty("jpackage.app-version", "dev"),
        )
    }

    private fun connect(
        paramsNode: JsonNode,
    ): ChargePointStateResult {
        val params = bind<IdentityParams>(paramsNode)
        val chargePoint = chargePointService.getByIdentity(params.identity)
        connectionManager.connect(chargePoint.idValue)
        return chargePoint.toResult()
    }

    private suspend fun disconnect(
        paramsNode: JsonNode,
    ): ChargePointStateResult {
        val params = bind<IdentityParams>(paramsNode)
        val chargePoint = chargePointService.getByIdentity(params.identity)
        connectionManager.disconnect(chargePoint.idValue)?.join()
        return chargePoint.toResult()
    }

    private suspend fun setAvailability(
        paramsNode: JsonNode,
    ): ChargePointStateResult {
        val params = bind<SetAvailabilityParams>(paramsNode)
        val chargePoint = chargePointService.getByIdentity(params.identity)
        chargePoint.setStatus(status = params.status, forceUpdate = true)
        return chargePoint.toResult()
    }

    private fun getChargePointState(
        paramsNode: JsonNode,
    ): ChargePointStateResult {
        val params = bind<IdentityParams>(paramsNode)
        return chargePointService.getByIdentity(params.identity).toResult()
    }

    private suspend fun setCarState(
        paramsNode: JsonNode,
    ): ConnectorStateResult {
        val params = bind<SetCarStateParams>(paramsNode)
        val chargePoint = chargePointService.getByIdentity(params.identity)
        val connector = chargePoint.getConnector(params.connectorId)
        connector.setConnectorCarState(params.carState)
        return connector.toResult()
    }

    private suspend fun setConnectorStatus(
        paramsNode: JsonNode,
    ): ConnectorStateResult {
        val params = bind<SetConnectorStatusParams>(paramsNode)
        val chargePoint = chargePointService.getByIdentity(params.identity)
        val connector = chargePoint.getConnector(params.connectorId)
        connector.setStatus(
            status = params.status,
            errorCode = params.errorCode,
            vendorId = params.vendorId,
            vendorErrorCode = params.vendorErrorCode,
            info = params.info,
            forceUpdate = true,
        )
        return connector.toResult()
    }

    private suspend fun authorize(
        paramsNode: JsonNode,
    ): AuthorizeResult {
        val params = bind<AuthorizeParams>(paramsNode)
        val chargePoint = chargePointService.getByIdentity(params.identity)
        val connector = chargePoint.getConnector(params.connectorId)
        val status = chargePointManager.authorize(connector, params.idTag)
        return AuthorizeResult(status)
    }

    private suspend fun stopTransaction(
        paramsNode: JsonNode,
    ): ConnectorStateResult {
        val params = bind<StopTransactionParams>(paramsNode)
        val chargePoint = chargePointService.getByIdentity(params.identity)
        val connector = chargePoint.getConnector(params.connectorId)
        connector.stopActiveTransactions(reason = params.reason, endReasonDescription = params.endReasonDescription)
        return connector.toResult()
    }

    private fun getConnectorState(
        paramsNode: JsonNode,
    ): ConnectorStateResult {
        val params = bind<ConnectorIdentityParams>(paramsNode)
        val chargePoint = chargePointService.getByIdentity(params.identity)
        return chargePoint.getConnector(params.connectorId).toResult()
    }

    private fun ChargePointDAO.toResult(): ChargePointStateResult {
        return ChargePointStateResult(
            identity = identity,
            connected = connected,
            status = status,
            errorCode = errorCode,
            connectors = getConnectors().map { it.toResult() },
        )
    }

    private fun ChargePointConnectorDAO.toResult(): ConnectorStateResult {
        return ConnectorStateResult(
            connectorId = position,
            status = status,
            carState = carState,
            activeTransactionId = activeTransactionId?.value,
            meterWh = meterWh,
            locked = locked,
        )
    }

    private inline fun <reified T> bind(
        node: JsonNode,
    ): T {
        return try {
            objectMapper.treeToValue(node, T::class.java)
        } catch (exception: Exception) {
            throw ControlCommandException("INVALID_PARAMS", exception.message ?: "invalid params for command")
        }
    }
}
