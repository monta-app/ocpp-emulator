package com.monta.ocpp.emulator.v16.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.smartcharge.ChargingProfileKindType
import com.monta.library.ocpp.v16.smartcharge.ChargingProfilePurposeType
import com.monta.library.ocpp.v16.smartcharge.ClearChargingProfileConfirmation
import com.monta.library.ocpp.v16.smartcharge.ClearChargingProfileRequest
import com.monta.library.ocpp.v16.smartcharge.ClearChargingProfileStatus
import com.monta.library.ocpp.v16.smartcharge.GetCompositeScheduleConfirmation
import com.monta.library.ocpp.v16.smartcharge.GetCompositeScheduleRequest
import com.monta.library.ocpp.v16.smartcharge.GetCompositeScheduleStatus
import com.monta.library.ocpp.v16.smartcharge.SetChargingProfileConfirmation
import com.monta.library.ocpp.v16.smartcharge.SetChargingProfileRequest
import com.monta.library.ocpp.v16.smartcharge.SetChargingProfileStatus
import com.monta.library.ocpp.v16.smartcharge.SmartChargeClientProfile
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointtransaction.service.ChargePointTransactionService
import com.monta.ocpp.emulator.logger.GlobalLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class SmartChargingClientHandler(
    private val chargePointService: ChargePointService,
    private val chargePointTransactionService: ChargePointTransactionService
) : SmartChargeClientProfile.Listener {

    override suspend fun clearChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: ClearChargingProfileRequest
    ): ClearChargingProfileConfirmation {
        val connector = getConnector(
            ocppSessionInfo = ocppSessionInfo,
            connectorId = request.connectorId
        )

        if (connector != null) {
            transaction {
                connector.transactions.forEach { transaction ->
                    transaction.clearChargingProfile()
                }
                connector.updateKw()
            }
        }

        return ClearChargingProfileConfirmation(
            status = ClearChargingProfileStatus.Accepted
        )
    }

    override suspend fun getCompositeSchedule(
        ocppSessionInfo: OcppSession.Info,
        request: GetCompositeScheduleRequest
    ): GetCompositeScheduleConfirmation {
        return GetCompositeScheduleConfirmation(
            status = GetCompositeScheduleStatus.Rejected
        )
    }

    override suspend fun setChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: SetChargingProfileRequest
    ): SetChargingProfileConfirmation {
        return if (setChargingProfileForTransaction(ocppSessionInfo, request)) {
            SetChargingProfileConfirmation(
                status = SetChargingProfileStatus.Accepted
            )
        } else {
            SetChargingProfileConfirmation(
                status = SetChargingProfileStatus.Rejected
            )
        }
    }

    private suspend fun setChargingProfileForTransaction(
        ocppSessionInfo: OcppSession.Info,
        request: SetChargingProfileRequest
    ): Boolean {
        val connector = getConnector(
            ocppSessionInfo = ocppSessionInfo,
            connectorId = request.connectorId
        )

        val chargingProfile = request.csChargingProfiles

        val transactionId: Int? = request.csChargingProfiles.transactionId

        if (connector == null) {
            return false
        }

        if (transactionId == null) {
            GlobalLogger.warn(connector, "rejected charging profile, no transaction provided")
            return false
        }

        if (chargingProfile.chargingProfilePurpose != ChargingProfilePurposeType.TxProfile) {
            GlobalLogger.warn(connector, "rejected charging profile, chargingProfilePurpose is not TxProfile")
            return false
        }

        if (chargingProfile.chargingProfileKind != ChargingProfileKindType.Absolute) {
            GlobalLogger.warn(connector, "rejected charging profile, chargingProfileKind is not absolute")
            return false
        }

        val transaction = chargePointTransactionService.getByExternalId(
            externalId = transactionId
        )

        if (transaction == null) {
            GlobalLogger.warn(connector, "rejected charging profile, no transaction found")
            return false
        }

        if (!transaction.isOwner(connector)) {
            GlobalLogger.warn(connector, "rejected charging profile, transaction not owned by cp")
            return false
        }

        GlobalLogger.info(transaction, "received charging profile :)")

        transaction {
            transaction.chargingProfile = request.csChargingProfiles
        }

        return true
    }

    private fun getConnector(
        ocppSessionInfo: OcppSession.Info,
        connectorId: Int?
    ): ChargePointConnectorDAO? {
        if (connectorId != null && connectorId > 0) {
            val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
            return chargePoint.getConnector(connectorId)
        }
        return null
    }
}
