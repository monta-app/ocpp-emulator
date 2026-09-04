package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.der.DerClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDERControlRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearDERControlResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDERControlRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetDERControlResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDERControlRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDERControlResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DERControlStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class DerHandler : DerClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun setDERControl(
        ocppSessionInfo: OcppSession.Info,
        request: SetDERControlRequest,
    ): SetDERControlResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("SetDERControl", request)
        stateStore.derControls(chargePoint.idValue)[request.controlId] = request
        return SetDERControlResponse(status = DERControlStatusEnum.Accepted)
    }

    override suspend fun getDERControl(
        ocppSessionInfo: OcppSession.Info,
        request: GetDERControlRequest,
    ): GetDERControlResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("GetDERControl", request)
        return GetDERControlResponse(status = DERControlStatusEnum.Accepted)
    }

    override suspend fun clearDERControl(
        ocppSessionInfo: OcppSession.Info,
        request: ClearDERControlRequest,
    ): ClearDERControlResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("ClearDERControl", request)
        val store = stateStore.derControls(chargePoint.idValue)
        val id = request.controlId
        if (id == null) {
            store.clear()
        } else {
            store.remove(id)
        }
        return ClearDERControlResponse(status = DERControlStatusEnum.Accepted)
    }
}
