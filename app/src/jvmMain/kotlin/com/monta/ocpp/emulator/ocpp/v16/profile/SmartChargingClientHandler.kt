package com.monta.ocpp.emulator.ocpp.v16.profile

import com.monta.library.ocpp.common.chargingprofile.ChargingProfileKind
import com.monta.library.ocpp.common.chargingprofile.ChargingRateUnit
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
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
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.transaction.service.ChargePointTransactionService
import com.monta.ocpp.emulator.chargepoint.txdefault.service.TxDefaultService
import com.monta.ocpp.emulator.ocpp.v16.smartcharging.ChargePointMaxProfileStore
import com.monta.ocpp.emulator.ocpp.v16.smartcharging.CompositeScheduleBuilder
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.ZonedDateTime
import javax.inject.Singleton

@Singleton
class SmartChargingClientHandler(
    private val chargePointService: ChargePointService,
    private val chargePointTransactionService: ChargePointTransactionService,
    private val txDefaultService: TxDefaultService,
    private val maxProfileStore: ChargePointMaxProfileStore,
) : SmartChargeClientProfile.Listener {

    override suspend fun clearChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: ClearChargingProfileRequest,
    ): ClearChargingProfileConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val connector = getConnector(
            ocppSessionInfo = ocppSessionInfo,
            connectorId = request.connectorId,
        )

        if (connector != null) {
            transaction {
                connector.transactions.forEach { transaction ->
                    transaction.clearChargingProfile()
                }
                connector.updateKw()
            }
        }

        txDefaultService.clear(chargePoint, connector, request)

        if (request.chargingProfilePurpose == null ||
            request.chargingProfilePurpose == ChargingProfilePurposeType.ChargePointMaxProfile
        ) {
            maxProfileStore.clear(
                chargePointId = chargePoint.id.value,
                chargingProfileId = request.id,
                stackLevel = request.stackLevel,
            )
        }

        return ClearChargingProfileConfirmation(
            status = ClearChargingProfileStatus.Accepted,
        )
    }

    override suspend fun getCompositeSchedule(
        ocppSessionInfo: OcppSession.Info,
        request: GetCompositeScheduleRequest,
    ): GetCompositeScheduleConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val connector = chargePoint.getConnector(request.connectorId)
            ?: return GetCompositeScheduleConfirmation(status = GetCompositeScheduleStatus.Rejected)

        val scheduleStart = ZonedDateTime.now()
        val profiles = mutableListOf<ChargingProfile>()

        maxProfileStore.get(chargePoint.id.value)?.let { profiles.add(it) }

        transaction {
            connector.activeTransaction?.chargingProfile?.let { profiles.add(it) }
        }

        val schedule = CompositeScheduleBuilder.build(
            profiles = profiles,
            durationSeconds = request.duration,
            scheduleStart = scheduleStart,
            preferredUnit = request.chargingRateUnit ?: ChargingRateUnit.W,
            fallbackMaxWatts = connector.maxKw * 1000.0,
        ) ?: return GetCompositeScheduleConfirmation(status = GetCompositeScheduleStatus.Rejected)

        return GetCompositeScheduleConfirmation(
            status = GetCompositeScheduleStatus.Accepted,
            connectorId = request.connectorId,
            scheduleStart = scheduleStart,
            chargingSchedule = schedule,
        )
    }

    override suspend fun setChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: SetChargingProfileRequest,
    ): SetChargingProfileConfirmation {
        return when {
            setChargePointMaxProfile(ocppSessionInfo, request) -> {
                SetChargingProfileConfirmation(status = SetChargingProfileStatus.Accepted)
            }
            setChargingProfileForTransaction(ocppSessionInfo, request) -> {
                SetChargingProfileConfirmation(status = SetChargingProfileStatus.Accepted)
            }
            setDefaultChargingProfile(ocppSessionInfo, request) -> {
                SetChargingProfileConfirmation(status = SetChargingProfileStatus.Accepted)
            }
            else -> {
                SetChargingProfileConfirmation(status = SetChargingProfileStatus.Rejected)
            }
        }
    }

    private suspend fun setChargePointMaxProfile(
        ocppSessionInfo: OcppSession.Info,
        request: SetChargingProfileRequest,
    ): Boolean {
        val profile = request.csChargingProfiles
        if (profile.chargingProfilePurpose != ChargingProfilePurposeType.ChargePointMaxProfile) {
            return false
        }
        if (request.connectorId != 0) {
            return false
        }
        if (!isSupportedKind(profile.chargingProfileKind)) {
            return false
        }
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        maxProfileStore.put(chargePoint.id.value, profile)
        GlobalLogger.info(chargePoint, "stored ChargePointMaxProfile id=${profile.chargingProfileId}")
        return true
    }

    private suspend fun setDefaultChargingProfile(
        ocppSessionInfo: OcppSession.Info,
        request: SetChargingProfileRequest,
    ): Boolean {
        if (request.csChargingProfiles.chargingProfilePurpose != ChargingProfilePurposeType.TxDefaultProfile) {
            return false
        }

        val connector = getConnector(
            ocppSessionInfo = ocppSessionInfo,
            connectorId = request.connectorId,
        ) ?: return false

        if (request.connectorId != 0) {
            GlobalLogger.warn(connector, "TxDefault must be on connector 0")
            return false
        }
        if (!isSupportedKind(request.csChargingProfiles.chargingProfileKind)) {
            GlobalLogger.warn(connector, "unsupported chargingProfileKind=${request.csChargingProfiles.chargingProfileKind}")
            return false
        }

        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        GlobalLogger.info(connector, "received TxDefault charging profile")
        transaction {
            txDefaultService.store(chargePoint, connector, request.csChargingProfiles)
        }
        return true
    }

    private suspend fun setChargingProfileForTransaction(
        ocppSessionInfo: OcppSession.Info,
        request: SetChargingProfileRequest,
    ): Boolean {
        if (request.csChargingProfiles.chargingProfilePurpose != ChargingProfilePurposeType.TxProfile) {
            return false
        }
        if (request.connectorId < 1) {
            return false
        }

        val connector = getConnector(
            ocppSessionInfo = ocppSessionInfo,
            connectorId = request.connectorId,
        ) ?: return false

        val chargingProfile = request.csChargingProfiles
        val transactionId = chargingProfile.transactionId
        if (transactionId == null) {
            GlobalLogger.warn(connector, "rejected charging profile, no transaction provided")
            return false
        }
        if (!isSupportedKind(chargingProfile.chargingProfileKind)) {
            GlobalLogger.warn(connector, "unsupported chargingProfileKind=${chargingProfile.chargingProfileKind}")
            return false
        }

        val transaction = chargePointTransactionService.getByExternalId(
            externalId = transactionId,
        )
        if (transaction == null) {
            GlobalLogger.warn(connector, "rejected charging profile, no transaction found")
            return false
        }
        if (!transaction.isOwner(connector)) {
            GlobalLogger.warn(connector, "rejected charging profile, transaction not owned by cp")
            return false
        }

        GlobalLogger.info(transaction, "received charging profile")
        transaction {
            transaction.chargingProfile = request.csChargingProfiles
        }
        return true
    }

    private fun isSupportedKind(
        kind: ChargingProfileKind?,
    ): Boolean {
        return kind == ChargingProfileKind.Absolute ||
            kind == ChargingProfileKind.Relative ||
            kind == ChargingProfileKind.Recurring
    }

    private fun getConnector(
        ocppSessionInfo: OcppSession.Info,
        connectorId: Int?,
    ): ChargePointConnectorDAO? {
        if (connectorId != null) {
            val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
            return chargePoint.getConnector(connectorId)
        }
        return null
    }
}
