package com.monta.ocpp.emulator.v16

import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.MeterValue
import com.monta.library.ocpp.v16.core.MeterValuesRequest
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.logger.GlobalLogger
import com.monta.ocpp.emulator.v16.util.MeterValuesGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Factory
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@Factory
class SchedulerService(
    private val chargePointId: Long,
) {

    private val logger = KotlinLogging.logger {}
    private val ocppClientV16: OcppClientV16 by injectAnywhere()
    private val chargePointService: ChargePointService by injectAnywhere()

    private val chargePoint: ChargePointDAO
        get() = chargePointService.getById(chargePointId)

    private val heartbeatInterval: Long
        get() = chargePoint.configuration.heartbeatInterval

    private val meterValueSampleInterval: Long
        get() = chargePoint.configuration.meterValueSampleInterval

    private val meterValuesSampledData: List<String>
        get() = chargePoint.configuration.meterValuesSampledData

    private var job: Job? = null

    fun start() {
        logger.info { "starting scheduler" }
        job?.cancel()
        job = launchThread(restart = true) {
            while (true) {
                delay(1000)
                yield()
                if (!chargePoint.connected) {
                    continue
                }
                heartbeat()
                handleActiveTransactions()
            }
        }
    }

    fun stop() {
        logger.info { "stopping scheduler" }
        job?.cancel()
    }

    private suspend fun heartbeat() {
        if (heartbeatInterval == 0L) {
            logger.trace { "heartbeat disabled" }
            return
        }

        val elapsedTime = Duration.between(
            chargePoint.heartbeatAt,
            Instant.now(),
        )

        if (elapsedTime.seconds < heartbeatInterval) {
            return
        }

        try {
            val ocppSession = ocppClientV16.getSessionByIdentity(chargePoint.identity)
            ocppClientV16.asCoreProfile(ocppSession.info).heartbeat()
        } catch (exception: Exception) {
            logger.warn(exception) { "failed to send heartbeat" }
            GlobalLogger.warn(chargePoint, "Failed to send heartbeat")
        }

        transaction {
            chargePoint.heartbeatAt = Instant.now()
        }
    }

    private suspend fun handleActiveTransactions() {
        val transactions = transaction { chargePoint.getActiveTransactions() }
        for (transaction in transactions) {
            try {
                handleTransaction(transaction)
            } catch (exception: Exception) {
                logger.error(exception) { "failed to update transaction id=${transaction.id}" }
            }
        }
    }

    private suspend fun handleTransaction(
        transaction: ChargePointTransactionDAO,
    ) {
        val connector = transaction {
            transaction.chargePointConnector
        }

        transaction {
            connector.updateKw(transaction.getChargingProfileWatts())
        }

        connector.setStatus(connector.calculateState())
        // Only increment meter if the charge point is in a charging state
        if (connector.status == ChargePointStatus.Charging) {
            val secondsSinceEndMeter = Duration.between(
                transaction.endMeterAt,
                Instant.now(),
            ).toSeconds()

            val newMeterValue = connector.wattHoursPerSecond * secondsSinceEndMeter.toDouble()

            transaction {
                transaction.endMeter += newMeterValue.roundToInt()
                transaction.chargePointConnector.meterAt = Instant.now()
            }
        }

        transaction {
            transaction.endMeterAt = Instant.now()
        }

        sendMeterValues(transaction, connector.kw * 1000, connector.vehicleNumberPhases)
    }

    private suspend fun sendMeterValues(
        transaction: ChargePointTransactionDAO,
        watts: Double,
        vehicleNumberPhases: Int,
    ) {
        if (meterValueSampleInterval == 0L) {
            logger.trace { "meterValues disabled" }
            return
        }

        val secondsSinceMeterValues = Duration.between(
            transaction.meterValuesAt,
            Instant.now(),
        ).toSeconds()

        if (secondsSinceMeterValues < meterValueSampleInterval) {
            return
        }

        val ocppSession = ocppClientV16.getSessionByIdentity(chargePoint.identity)

        ocppClientV16.asCoreProfile(ocppSession.info).meterValues(
            MeterValuesRequest(
                connectorId = transaction.connectorPosition(),
                transactionId = transaction.externalId,
                meterValue = listOf(
                    MeterValue(
                        timestamp = ZonedDateTime.now(),
                        sampledValue = MeterValuesGenerator.generate(
                            meterValuesSampledData = meterValuesSampledData,
                            startTime = transaction.startTime,
                            endMeter = transaction.endMeter,
                            watts = watts,
                            numberPhases = vehicleNumberPhases,
                        ),
                    ),
                ),
            ),
        )

        transaction {
            transaction.meterValuesAt = Instant.now()
        }

        GlobalLogger.info(transaction, "Meter values sent")
    }
}
