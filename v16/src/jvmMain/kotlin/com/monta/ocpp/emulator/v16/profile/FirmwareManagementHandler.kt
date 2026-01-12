package com.monta.ocpp.emulator.v16.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.firmware.FirmwareManagementClientProfile
import com.monta.library.ocpp.v16.firmware.GetDiagnosticsConfirmation
import com.monta.library.ocpp.v16.firmware.GetDiagnosticsRequest
import com.monta.library.ocpp.v16.firmware.UpdateFirmwareConfirmation
import com.monta.library.ocpp.v16.firmware.UpdateFirmwareRequest
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.ChargePointManager
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import javax.inject.Singleton

@Singleton
class FirmwareManagementHandler : FirmwareManagementClientProfile.Listener {

    private val logger = KotlinLogging.logger {}

    private val chargePointManager: ChargePointManager by injectAnywhere()
    private val chargePointService: ChargePointService by injectAnywhere()

    override suspend fun getDiagnostics(
        ocppSessionInfo: OcppSession.Info,
        request: GetDiagnosticsRequest
    ): GetDiagnosticsConfirmation {
        return GetDiagnosticsConfirmation()
    }

    override suspend fun updateFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: UpdateFirmwareRequest
    ): UpdateFirmwareConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        if (!chargePoint.canPerformAction) {
            return UpdateFirmwareConfirmation
        }

        try {
            return UpdateFirmwareConfirmation
        } finally {
            startFirmwareUpdate(chargePoint, request.location)
        }
    }

    fun startFirmwareUpdate(
        chargePoint: ChargePointDAO,
        location: String
    ) {
        launchThread {
            val parts = location.split("?")
            if (parts.size != 2) {
                logger.error("no parameters in url in from upgrade trigger")
                return@launchThread
            }
            val (_, parameters) = parts
            val firmwareVersion = parameters.parseUrlEncodedParameters()["version"]

            if (firmwareVersion == null) {
                logger.error("missing version from upgrade trigger")
                return@launchThread
            }

            chargePointManager.startFirmwareUpdate(
                chargePoint = chargePoint,
                firmwareVersion = firmwareVersion
            )
        }
    }
}
