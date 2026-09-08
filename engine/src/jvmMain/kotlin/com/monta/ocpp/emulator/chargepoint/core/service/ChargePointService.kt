package com.monta.ocpp.emulator.chargepoint.core.service

import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.exception.ChargePointNotFoundException
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.inject.Singleton

@Singleton
class ChargePointService(
    private val chargePointRepository: ChargePointRepository,
) {

    fun getById(
        id: Long,
    ): ChargePointDAO = transaction {
        val chargePoint = chargePointRepository.getById(id)
        if (chargePoint == null) throw ChargePointNotFoundException()
        return@transaction chargePoint
    }

    fun getByIdentity(
        identity: String,
    ): ChargePointDAO = transaction {
        val chargePoint = chargePointRepository.getByIdentity(identity)
        if (chargePoint == null) throw ChargePointNotFoundException()
        return@transaction chargePoint
    }

    /** Whether a charge point already exists with the given identity (compared in normalised form). */
    fun isIdentityInUse(
        identity: String,
    ): Boolean = transaction {
        chargePointRepository.getByIdentity(identity) != null
    }

    /** The stored (trimmed, upper-cased) form of an identity, for callers that need to match it. */
    fun normalizeIdentity(
        identity: String,
    ): String = ChargePointDAO.normalizeIdentity(identity)

    fun upsert(
        name: String,
        identity: String,
        password: String?,
        ocppUrl: String,
        apiUrl: String,
        firmware: String,
        maxKw: Double,
        connectorCount: Int,
        meterType: MeterType,
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
                maxKw = maxKw,
                meterType = meterType,
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
        block: ChargePointDAO.() -> Unit,
    ): ChargePointDAO {
        return transaction {
            block(chargePoint)
            chargePoint
        }
    }

    /**
     * Permanently removes a charge point together with its connectors and their transactions.
     * Children are deleted before the parent so a foreign-key constraint can never be left dangling.
     */
    fun delete(
        id: Long,
    ) {
        transaction {
            val chargePoint = chargePointRepository.getById(id) ?: throw ChargePointNotFoundException()
            chargePoint.connectors.forEach { connector ->
                connector.transactions.forEach { transaction ->
                    transaction.delete()
                }
                connector.delete()
            }
            chargePoint.delete()
        }
    }
}
