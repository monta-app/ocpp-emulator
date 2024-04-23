package com.monta.ocpp.emulator.v16

import com.monta.library.ocpp.v16.AuthorizationStatus
import com.monta.library.ocpp.v16.client.OcppClientV16
import com.monta.library.ocpp.v16.core.AuthorizeRequest
import com.monta.library.ocpp.v16.core.BootNotificationRequest
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.DataTransferRequest
import com.monta.library.ocpp.v16.core.RegistrationStatus
import com.monta.library.ocpp.v16.firmware.DiagnosticsStatusNotificationRequest
import com.monta.library.ocpp.v16.firmware.DiagnosticsStatusNotificationStatus
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationRequest
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationStatus
import com.monta.library.ocpp.v16.security.SecurityEventNotificationRequest
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.view.components.security.SecurityEvent
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.logger.GlobalLogger
import com.monta.ocpp.emulator.v16.connection.ConnectionManager
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import java.time.Instant
import java.time.ZonedDateTime

@Singleton
class ChargePointManager {

    private val logger = KotlinLogging.logger {}
    private val ocppClientV16: OcppClientV16 by injectAnywhere()
    private val chargePointService: ChargePointService by injectAnywhere()
    private val connectionManager: ConnectionManager by injectAnywhere()

    suspend fun startBootSequence(
        chargePoint: ChargePointDAO
    ) {
        val confirmation = ocppClientV16.asCoreProfile(chargePoint.sessionInfo).bootNotification(
            request = BootNotificationRequest(
                chargePointSerialNumber = chargePoint.serial,
                firmwareVersion = chargePoint.firmware
            )
        )

        when (confirmation.status) {
            RegistrationStatus.Accepted -> {
                GlobalLogger.info(chargePoint, "Boot was accepted")

                chargePointService.update(chargePoint) {
                    this.updateConfiguration {
                        heartbeatInterval = confirmation.interval.toLong()
                    }
                    this.heartbeatAt = Instant.now()
                }

                chargePoint.setStatus(
                    status = ChargePointStatus.Available,
                    errorCode = ChargePointErrorCode.NoError,
                    forceUpdate = true
                )

                eichrechtDataTransfer(chargePoint)

                for (connector in chargePoint.getConnectors()) {
                    connector.setStatuses(
                        ChargePointStatus.Available,
                        connector.calculateState()
                    )
                }
            }

            RegistrationStatus.Pending -> {
                // Do nothing?
                GlobalLogger.info(chargePoint, "Boot is pending, waiting for server")
            }

            RegistrationStatus.Rejected -> {
                GlobalLogger.info(
                    chargePoint,
                    "Boot was rejected, will try to reconnect in ${confirmation.interval} seconds"
                )

                connectionManager.reconnect(
                    chargePointId = chargePoint.idValue,
                    delayInSeconds = confirmation.interval
                )
            }
        }
    }

    private suspend fun eichrechtDataTransfer(
        chargePoint: ChargePointDAO
    ) {
        val eichrechtKey = chargePoint.configuration.eichrechtKey
        chargePointService.update(chargePoint) {
            // if the key was newly created we have to persist it
            chargePoint.updateConfiguration {
                this.eichrechtKey = eichrechtKey
            }
        }

        val publicKey = eichrechtKey.publicKey()
        ocppClientV16.asCoreProfile(
            ocppSessionInfo = chargePoint.sessionInfo
        ).dataTransfer(
            request = DataTransferRequest(
                vendorId = "generalConfiguration",
                messageId = "setMeterConfiguration",
                data = """{"meters":[""" + chargePoint.getConnectors().joinToString(",") { connector ->
                    """{"connectorId":${connector.position},"meterSerial":"${chargePoint.serial}","type":"SIGNATURE","publicKey":"$publicKey"}"""
                } + """]}"""
            )
        )
    }

    suspend fun heartbeat(
        chargePoint: ChargePointDAO
    ) {
        try {
            ocppClientV16.asCoreProfile(
                ocppSessionInfo = chargePoint.sessionInfo
            ).heartbeat()
        } catch (exception: Exception) {
            logger.warn("failed to send heartbeat", exception)
            GlobalLogger.warn(chargePoint, "Failed to send heartbeat")
        }
    }

    suspend fun authorize(
        connector: ChargePointConnectorDAO,
        idTag: String
    ): AuthorizationStatus {
        GlobalLogger.info(connector, "Attempting to authorize with idTag $idTag")

        val confirmation = ocppClientV16.asCoreProfile(connector.sessionInfo).authorize(
            AuthorizeRequest(
                idTag = idTag
            )
        )

        when (confirmation.idTagInfo.status) {
            AuthorizationStatus.Accepted -> {
                GlobalLogger.info(connector, "Authorization IdTag was accepted")
                connector.start(idTag)
            }

            AuthorizationStatus.Blocked -> {
                GlobalLogger.warn(connector, "Authorization IdTag is blocked")
            }

            AuthorizationStatus.Expired -> {
                GlobalLogger.warn(connector, "Authorization IdTag has expired")
            }

            AuthorizationStatus.Invalid -> {
                GlobalLogger.warn(connector, "Authorization IdTag was invalid")
            }

            AuthorizationStatus.ConcurrentTx -> {
                GlobalLogger.error(connector, "Authorization not allowed while transaction is ongoing")
            }
        }

        return confirmation.idTagInfo.status
    }

    suspend fun startFirmwareUpdate(
        chargePoint: ChargePointDAO,
        firmwareVersion: String
    ) {
        GlobalLogger.info(chargePoint, "Updating charge point to version $firmwareVersion")

        val statuses = listOf(
            FirmwareStatusNotificationStatus.Downloading,
            FirmwareStatusNotificationStatus.Downloaded,
            FirmwareStatusNotificationStatus.Installing,
            FirmwareStatusNotificationStatus.Installed
        )

        for (status in statuses) {
            firmwareStatusNotification(chargePoint, status)
            delay(5000)
        }

        chargePointService.update(chargePoint) {
            this.firmware = firmwareVersion
        }

        GlobalLogger.info(chargePoint, "Updating charge point to version $firmwareVersion")

        startBootSequence(chargePoint)
    }

    suspend fun firmwareStatusNotification(
        chargePoint: ChargePointDAO,
        status: FirmwareStatusNotificationStatus
    ) {
        try {
            ocppClientV16.asFirmwareProfile(chargePoint.sessionInfo).firmwareStatusNotification(
                FirmwareStatusNotificationRequest(status)
            )

            chargePointService.update(chargePoint) {
                this.firmwareStatus = status
            }

            GlobalLogger.info(chargePoint, "Firmware status set to $status")
        } catch (t: Throwable) {
            logger.warn { "Failed to send firmware status notification, $t" }
        }
    }

    suspend fun diagnosticsStatusNotification(
        chargePoint: ChargePointDAO,
        status: DiagnosticsStatusNotificationStatus
    ) {
        try {
            ocppClientV16.asFirmwareProfile(chargePoint.sessionInfo).diagnosticsStatusNotification(
                DiagnosticsStatusNotificationRequest(status)
            )

            chargePointService.update(chargePoint) {
                this.diagnosticsStatus = status
            }

            GlobalLogger.info(chargePoint, "Firmware status set to $status")
        } catch (t: Throwable) {
            logger.warn { "Failed to send diagnostics status notification, $t" }
        }
    }

    /**
     * Transaction Stuff
     */

    suspend fun securityEvent(
        chargePoint: ChargePointDAO,
        securityEvent: SecurityEvent,
        techInfo: String? = null
    ) {
        try {
            ocppClientV16.asSecurityProfile(chargePoint.sessionInfo).securityEventNotification(
                request = SecurityEventNotificationRequest(
                    type = securityEvent.name,
                    timestamp = ZonedDateTime.now(),
                    techInfo = if (techInfo.isNullOrBlank()) null else techInfo
                )
            )
        } catch (exception: Exception) {
            GlobalLogger.error(chargePoint, "Failed to send security event")
        }

        GlobalLogger.info(chargePoint, "Security event sent")
    }
}
