package com.monta.ocpp.emulator.chargepoint.service

import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.exception.ChargePointNotFoundException
import com.monta.ocpp.emulator.chargepoint.repository.ChargePointRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class ChargePointService(
    private val chargePointRepository: ChargePointRepository
) {

    fun getById(
        id: Long
    ): ChargePointDAO = transaction {
        val chargePoint = chargePointRepository.getById(id)
        if (chargePoint == null) throw ChargePointNotFoundException()
        return@transaction chargePoint
    }

    fun getByIdentity(
        identity: String
    ): ChargePointDAO = transaction {
        val chargePoint = chargePointRepository.getByIdentity(identity)
        if (chargePoint == null) throw ChargePointNotFoundException()
        return@transaction chargePoint
    }

    fun upsert(
        name: String,
        identity: String,
        password: String?,
        ocppUrl: String,
        apiUrl: String,
        firmware: String,
        maxKw: Double,
        connectorCount: Int
    ): ChargePointDAO {
        return transaction {
            // Initialize our charge point
            val chargePoint = chargePointRepository.upsert(
                name = name,
                identity = identity,
                password = password,
                ocppUrl = ocppUrl,
                apiUrl = apiUrl,
                firmware = firmware,
                maxKw = maxKw
            )
            // Initialize our connectors
            for (connectorId in 1..connectorCount) {
                chargePoint.getConnector(connectorId)
            }
            val connectors = chargePoint.connectors
            connectors.filter { it.position > connectorCount }.forEach { connector ->
                connector.transactions.forEach { transaction ->
                    transaction.delete()
                }
                connector.delete()
            }
            return@transaction chargePoint
        }
    }

    fun update(
        chargePoint: ChargePointDAO,
        block: ChargePointDAO.() -> Unit
    ): ChargePointDAO {
        return transaction {
            block(chargePoint)
            chargePoint
        }
    }
}
