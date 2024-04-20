package com.monta.ocpp.emulator.v16.service.ocpp.profile.configuration

import com.monta.library.ocpp.v16.core.ChangeConfigurationConfirmation
import com.monta.library.ocpp.v16.core.ConfigurationStatus
import com.monta.library.ocpp.v16.core.Reason
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.logger.GlobalLogger
import com.monta.ocpp.emulator.v16.data.entity.ChargePointDAO
import com.monta.ocpp.emulator.v16.data.model.ChargePointConfiguration
import com.monta.ocpp.emulator.v16.data.service.ChargePointService
import com.monta.ocpp.emulator.v16.service.ocpp.startFreeCharging
import com.monta.ocpp.emulator.v16.service.ocpp.stopActiveTransactions
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class ChangeConfigurationService {

    private val chargePointService: ChargePointService by injectAnywhere()

    suspend fun changeConfiguration(
        chargePointIdentity: String,
        key: String,
        value: String?
    ): ChangeConfigurationConfirmation {
        val chargePoint = chargePointService.getByIdentity(chargePointIdentity)

        return when (key) {
            "AuthorizationKey" -> handleAuthorizationKey(chargePoint, value)
            ChargePointConfiguration.freeChargingKey -> handleFreeCharging(chargePoint, value)
            else -> {
                if (chargePoint.configuration.containsKey(key)) {
                    chargePointService.update(chargePoint) {
                        this.updateConfiguration { this[key] = value }
                    }
                    ChangeConfigurationConfirmation(
                        status = ConfigurationStatus.Accepted
                    )
                } else {
                    ChangeConfigurationConfirmation(
                        status = ConfigurationStatus.Rejected
                    )
                }
            }
        }
    }

    private suspend fun handleAuthorizationKey(
        chargePoint: ChargePointDAO,
        value: String?
    ): ChangeConfigurationConfirmation {
        GlobalLogger.info(chargePoint, "Security authorization updated")

        transaction {
            chargePoint.basicAuthPassword = value
        }

        return ChangeConfigurationConfirmation(
            status = ConfigurationStatus.Accepted
        )
    }

    private suspend fun handleFreeCharging(
        chargePoint: ChargePointDAO,
        value: String?
    ): ChangeConfigurationConfirmation {
        val freeChargingEnabled = value.equals("true", ignoreCase = true)

        transaction {
            chargePoint.updateConfiguration {
                this.freeCharging = freeChargingEnabled
            }
        }

        val chargePointConnectors = chargePoint.getConnectors()

        if (freeChargingEnabled) {
            GlobalLogger.info(chargePoint, "Free charging enabled")
            chargePointConnectors.forEach { connector ->
                connector.startFreeCharging()
            }
        } else {
            GlobalLogger.info(chargePoint, "Free charging disabled")
            chargePointConnectors.forEach { connector ->
                connector.stopActiveTransactions(
                    reason = Reason.DeAuthorized,
                    endReasonDescription = "free charging disabled"
                )
            }
        }

        return ChangeConfigurationConfirmation(
            status = ConfigurationStatus.Accepted
        )
    }
}
