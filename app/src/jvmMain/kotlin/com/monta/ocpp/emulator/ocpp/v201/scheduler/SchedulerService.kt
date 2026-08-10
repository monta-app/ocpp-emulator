package com.monta.ocpp.emulator.ocpp.v201.scheduler

import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.reservation.service.ChargePointReservationService
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.ocpp.v201.extension.setStatus201
import com.monta.ocpp.emulator.ocpp.v201.service.ChargePointManager
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.platform.util.launchThread
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToInt

class SchedulerService(
    private val chargePointId: Long,
) {
    private val logger = KotlinLogging.logger {}
    private val chargePointService: ChargePointService by injectAnywhere()
    private val chargePointManager: ChargePointManager by injectAnywhere()
    private val reservationService: ChargePointReservationService by injectAnywhere()

    private val chargePoint: ChargePointDAO
        get() = chargePointService.getById(chargePointId)

    private val heartbeatInterval: Long
        get() = chargePoint.configuration.heartbeatInterval

    private val meterValueSampleInterval: Long
        get() = chargePoint.configuration.meterValueSampleInterval

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
                reservationService.expireDueReservations()
            }
        }
    }

    fun stop() {
        logger.info { "stopping scheduler" }
        job?.cancel()
    }

    private suspend fun heartbeat() {
        if (heartbeatInterval == 0L) {
            return
        }
        val elapsedTime = Duration.between(chargePoint.heartbeatAt, Instant.now())
        if (elapsedTime.seconds < heartbeatInterval) {
            return
        }
        try {
            chargePointManager.heartbeat(chargePoint)
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
        for (tx in transactions) {
            try {
                handleTransaction(tx)
            } catch (exception: Exception) {
                logger.error(exception) { "failed to update transaction id=${tx.id}" }
            }
        }
    }

    private suspend fun handleTransaction(
        tx: ChargePointTransactionDAO,
    ) {
        val connector = transaction {
            tx.chargePointConnector
        }

        transaction {
            connector.updateKw(tx.getChargingProfileWatts())
        }

        connector.setStatus201(connector.calculateState())

        if (connector.status == ChargePointStatus.Charging) {
            val secondsSinceEndMeter = Duration.between(
                tx.endMeterAt,
                Instant.now(),
            ).toSeconds()
            val newMeterValue = connector.wattHoursPerSecond * secondsSinceEndMeter.toDouble()
            transaction {
                tx.endMeter += newMeterValue.roundToInt()
                tx.chargePointConnector.meterAt = Instant.now()
            }
        }

        transaction {
            tx.endMeterAt = Instant.now()
        }

        if (meterValueSampleInterval == 0L) {
            return
        }
        val secondsSinceMeterValues = Duration.between(tx.meterValuesAt, Instant.now()).toSeconds()
        if (secondsSinceMeterValues < meterValueSampleInterval) {
            return
        }
        try {
            chargePointManager.sendTransactionUpdate(tx)
            GlobalLogger.info(tx, "TransactionEvent Updated sent")
        } catch (exception: Exception) {
            logger.warn(exception) { "failed transaction update" }
        }
        transaction {
            tx.meterValuesAt = Instant.now()
        }
    }
}
