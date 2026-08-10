package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.extension.setStatus21
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.availability.AvailabilityClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeAvailabilityRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeAvailabilityResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ChangeAvailabilityStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.OperationalStatusEnum
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.platform.util.launchThread
import javax.inject.Singleton

@Singleton
class AvailabilityHandler : AvailabilityClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()

    override suspend fun changeAvailability(
        ocppSessionInfo: OcppSession.Info,
        request: ChangeAvailabilityRequest,
    ): ChangeAvailabilityResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        if (!chargePoint.canPerformAction) {
            return ChangeAvailabilityResponse(status = ChangeAvailabilityStatusEnum.Rejected)
        }
        val status = when (request.operationalStatus) {
            OperationalStatusEnum.Operative -> ChargePointStatus.Available
            OperationalStatusEnum.Inoperative -> ChargePointStatus.Unavailable
        }
        launchThread {
            val evseId = request.evse?.id
            if (evseId == null) {
                chargePoint.setStatus21(status = status, errorCode = ChargePointErrorCode.NoError)
                for (connector in chargePoint.getConnectors()) {
                    connector.setStatus21(status = status, forceUpdate = true)
                }
            } else {
                chargePoint.getConnector(evseId).setStatus21(status = status, forceUpdate = true)
            }
        }
        return ChangeAvailabilityResponse(status = ChangeAvailabilityStatusEnum.Accepted)
    }
}
