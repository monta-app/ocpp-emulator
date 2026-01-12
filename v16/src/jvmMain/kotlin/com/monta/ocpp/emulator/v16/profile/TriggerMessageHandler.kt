package com.monta.ocpp.emulator.v16.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageClientProfile
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageConfirmation
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageRequest
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageRequestType
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageStatus
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.chargepointconnector.service.ChargePointConnectorService
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.v16.ChargePointManager
import com.monta.ocpp.emulator.v16.setStatus
import javax.inject.Singleton

@Singleton
class TriggerMessageHandler : TriggerMessageClientProfile.Listener {

    private val chargePointManager: ChargePointManager by injectAnywhere()
    private val chargePointService: ChargePointService by injectAnywhere()
    private val chargePointConnectorService: ChargePointConnectorService by injectAnywhere()

    override suspend fun triggerMessage(
        ocppSessionInfo: OcppSession.Info,
        request: TriggerMessageRequest,
    ): TriggerMessageConfirmation {
        try {
            return TriggerMessageConfirmation(
                status = TriggerMessageStatus.Accepted,
            )
        } finally {
            launchThread {
                handleTriggerMessage(
                    ocppSessionInfo = ocppSessionInfo,
                    request = request,
                )
            }
        }
    }

    private suspend fun handleTriggerMessage(
        ocppSessionInfo: OcppSession.Info,
        request: TriggerMessageRequest,
    ) {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        when (request.requestedMessage) {
            TriggerMessageRequestType.BootNotification -> {
                chargePointManager.startBootSequence(chargePoint)
            }

            TriggerMessageRequestType.DiagnosticsStatusNotification -> {
                chargePointManager.diagnosticsStatusNotification(
                    chargePoint = chargePoint,
                    status = chargePoint.diagnosticsStatus,
                )
            }

            TriggerMessageRequestType.FirmwareStatusNotification -> {
                chargePointManager.firmwareStatusNotification(
                    chargePoint = chargePoint,
                    status = chargePoint.firmwareStatus,
                )
            }

            TriggerMessageRequestType.Heartbeat -> {
                chargePointManager.heartbeat(chargePoint)
            }

            TriggerMessageRequestType.MeterValues -> {
                // TODO implement
            }

            TriggerMessageRequestType.StatusNotification -> {
                triggerStatusNotification(request, chargePoint)
            }
        }
    }

    private suspend fun triggerStatusNotification(
        request: TriggerMessageRequest,
        chargePoint: ChargePointDAO,
    ) {
        val connectorId = request.connectorId ?: 0

        if (connectorId == 0) {
            chargePoint.setStatus(
                status = chargePoint.status,
                errorCode = chargePoint.errorCode,
            )
        } else {
            val connector = chargePointConnectorService.get(
                chargePointId = chargePoint.idValue,
                connectorId = connectorId,
            )
            if (connector == null) {
                return
            }
            connector.setStatus(
                status = connector.status,
                errorCode = connector.errorCode,
                vendorId = if (connector.vendorId.isNullOrBlank()) null else connector.vendorId,
                vendorErrorCode = if (connector.vendorErrorCode.isNullOrBlank()) null else connector.vendorErrorCode,
                info = if (connector.statusInfo.isNullOrBlank()) null else connector.statusInfo,
                forceUpdate = true,
            )
        }
    }
}
