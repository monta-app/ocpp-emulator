package com.monta.ocpp.emulator.testsupport

import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object ChargePointFixtures {

    private val chargePointService = ChargePointService(ChargePointRepository())

    fun newChargePoint(
        identity: String = "MEM_001",
        name: String = "Emulator",
        connectorCount: Int = 1,
        maxKw: Double = 22.0,
        meterType: MeterType = MeterType.OCPP,
    ): ChargePointDAO {
        return chargePointService.upsert(
            name = name,
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

    fun connectorOf(
        chargePoint: ChargePointDAO,
        position: Int = 1,
    ): ChargePointConnectorDAO {
        return transaction {
            chargePoint.connectors.first { connector -> connector.position == position }
        }
    }

    fun startTransaction(
        chargePoint: ChargePointDAO,
        connector: ChargePointConnectorDAO,
        externalId: Int = 1,
        idTag: String = "TAG",
    ): ChargePointTransactionDAO {
        return transaction {
            val chargePointTransaction = ChargePointTransactionDAO.newInstance(
                chargePoint = chargePoint,
                chargePointConnector = connector,
                externalId = externalId,
                idTag = idTag,
            )
            connector.activeTransaction = chargePointTransaction
            chargePointTransaction
        }
    }

    fun setConnectorState(
        connector: ChargePointConnectorDAO,
        block: ChargePointConnectorDAO.() -> Unit,
    ): ChargePointConnectorDAO {
        return transaction {
            block(connector)
            connector
        }
    }
}
