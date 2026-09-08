package com.monta.ocpp.emulator.chargepoint.core.repository

import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepoint.core.model.MeterType
import com.monta.ocpp.emulator.platform.database.extension.createDatabaseListener
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import javax.inject.Singleton

@Singleton
class ChargePointRepository {

    fun upsert(
        name: String,
        identity: String,
        password: String?,
        ocppUrl: String,
        apiUrl: String,
        firmware: String,
        maxKw: Double,
        meterType: MeterType,
    ): ChargePointDAO {
        val chargePoint = getByIdentity(identity)

        if (chargePoint != null) {
            chargePoint.name = name
            chargePoint.basicAuthPassword = password
            chargePoint.ocppUrl = ocppUrl
            chargePoint.apiUrl = apiUrl
            chargePoint.firmware = firmware
            chargePoint.maxKw = maxKw
            chargePoint.meterType = meterType
            return chargePoint
        }

        return ChargePointDAO.newInstance(
            name = name,
            identity = identity,
            password = password,
            ocppUrl = ocppUrl,
            apiUrl = apiUrl,
            firmware = firmware,
            maxKw = maxKw,
            meterType = meterType,
        )
    }

    fun getAllFlow(): Flow<List<ChargePointDAO>> {
        return createDatabaseListener(
            entityClass = ChargePointDAO,
        ) {
            transaction {
                getAll()
            }
        }
    }

    fun getAll(): List<ChargePointDAO> {
        return ChargePointDAO.all()
            .toList()
    }

    fun getById(
        id: Long,
    ): ChargePointDAO? {
        return ChargePointDAO.find {
            ChargePointTable.id eq id
        }.firstOrNull()
    }

    fun getByIdFlow(
        id: Long,
    ): Flow<ChargePointDAO> {
        return createDatabaseListener(
            entityClass = ChargePointDAO,
            id = id,
        ) {
            transaction {
                getById(id)
            }
        }
    }

    fun getByIdentity(
        identity: String,
    ): ChargePointDAO? {
        // Rows always hold the normalised identity, so the lookup has to normalise as well —
        // otherwise a caller passing an un-normalised identity misses the row it is looking for
        return ChargePointDAO.find {
            ChargePointTable.identity eq ChargePointDAO.normalizeIdentity(identity)
        }.firstOrNull()
    }

    fun getConnectedChargePoints(): List<ChargePointDAO> {
        return transaction {
            println("fetching connected charge points")
            ChargePointDAO.find {
                ChargePointTable.connected eq true
            }.toList()
        }
    }

    fun clearChargePointBootStatus(
        chargePointId: Long,
    ) {
        transaction {
            ChargePointTable.update({ ChargePointTable.id eq chargePointId }) { statement ->
                statement[bootedAt] = null
            }
        }
    }
}
