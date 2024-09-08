package com.monta.ocpp.emulator.chargepoint.repository

import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointTable
import com.monta.ocpp.emulator.common.createDatabaseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Singleton

@Singleton
class ChargePointRepository {

    fun upsert(
        name: String,
        identity: String,
        password: String,
        ocppUrl: String,
        apiUrl: String,
        firmware: String,
        maxKw: Double
    ): ChargePointDAO {
        val chargePoint = ChargePointDAO.find {
            ChargePointTable.identity eq identity
        }.firstOrNull()

        if (chargePoint != null) {
            chargePoint.name = name
            chargePoint.basicAuthPassword = password
            chargePoint.ocppUrl = ocppUrl
            chargePoint.apiUrl = apiUrl
            chargePoint.firmware = firmware
            chargePoint.maxKw = maxKw
            return chargePoint
        }

        return ChargePointDAO.newInstance(
            name = name,
            identity = identity,
            password = password,
            ocppUrl = ocppUrl,
            apiUrl = apiUrl,
            firmware = firmware,
            maxKw = maxKw
        )
    }

    fun getAllFlow(
        coroutineScope: CoroutineScope
    ): Flow<List<ChargePointDAO>> {
        return createDatabaseListener(
            coroutineScope = coroutineScope,
            entityClass = ChargePointDAO
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
        id: Long
    ): ChargePointDAO? {
        return ChargePointDAO.find {
            ChargePointTable.id eq id
        }.firstOrNull()
    }

    fun getByIdFlow(
        coroutineScope: CoroutineScope,
        id: Long
    ): Flow<ChargePointDAO> {
        return createDatabaseListener(
            coroutineScope = coroutineScope,
            entityClass = ChargePointDAO,
            id = id
        ) {
            transaction {
                getById(id)
            }
        }
    }

    fun getByIdentity(
        identity: String
    ): ChargePointDAO? {
        return ChargePointDAO.find {
            ChargePointTable.identity eq identity
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
        chargePointId: Long
    ) {
        transaction {
            ChargePointTable.update({ ChargePointTable.id eq chargePointId }) {
                it[bootedAt] = null
            }
        }
    }
}
