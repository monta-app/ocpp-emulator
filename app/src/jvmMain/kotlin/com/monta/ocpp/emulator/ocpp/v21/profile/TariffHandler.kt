package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariff.TariffClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeTransactionTariffRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeTransactionTariffResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearTariffsRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearTariffsResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTariffsRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTariffsResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDefaultTariffRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDefaultTariffResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearTariffsResult
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TariffChangeStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TariffClearStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TariffGetStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.TariffSetStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class TariffHandler : TariffClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun setDefaultTariff(
        ocppSessionInfo: OcppSession.Info,
        request: SetDefaultTariffRequest,
    ): SetDefaultTariffResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("SetDefaultTariff", request)
        stateStore.tariffs(chargePoint.idValue)[request.tariff.tariffId] = request
        return SetDefaultTariffResponse(status = TariffSetStatusEnum.Accepted)
    }

    override suspend fun getTariffs(
        ocppSessionInfo: OcppSession.Info,
        request: GetTariffsRequest,
    ): GetTariffsResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("GetTariffs", request)
        return GetTariffsResponse(status = TariffGetStatusEnum.Accepted)
    }

    override suspend fun clearTariffs(
        ocppSessionInfo: OcppSession.Info,
        request: ClearTariffsRequest,
    ): ClearTariffsResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("ClearTariffs", request)
        val store = stateStore.tariffs(chargePoint.idValue)
        val ids = request.tariffIds
        val results = if (ids.isNullOrEmpty()) {
            store.clear()
            listOf(ClearTariffsResult(status = TariffClearStatusEnum.Accepted))
        } else {
            ids.map { id ->
                val removed = store.remove(id) != null
                ClearTariffsResult(
                    status = if (removed) TariffClearStatusEnum.Accepted else TariffClearStatusEnum.NoTariff,
                    tariffId = id,
                )
            }
        }
        return ClearTariffsResponse(clearTariffsResult = results)
    }

    override suspend fun changeTransactionTariff(
        ocppSessionInfo: OcppSession.Info,
        request: ChangeTransactionTariffRequest,
    ): ChangeTransactionTariffResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("ChangeTransactionTariff", request)
        stateStore.tariffs(chargePoint.idValue)["tx:${request.transactionId}"] = request
        return ChangeTransactionTariffResponse(status = TariffChangeStatusEnum.Accepted)
    }
}
