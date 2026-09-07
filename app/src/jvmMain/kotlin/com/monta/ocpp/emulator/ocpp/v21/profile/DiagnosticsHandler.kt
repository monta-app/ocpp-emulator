package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.diagnostics.DiagnosticsClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearVariableMonitoringRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearVariableMonitoringResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CustomerInformationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CustomerInformationResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLogRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLogResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetMonitoringReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetMonitoringReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringBaseRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringBaseResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringLevelRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetMonitoringLevelResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariableMonitoringRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariableMonitoringResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearMonitoringResult
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearMonitoringStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CustomerInformationStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericDeviceModelStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.LogStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class DiagnosticsHandler : DiagnosticsClientDispatcher.Listener {
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun clearVariableMonitoring(
        ocppSessionInfo: OcppSession.Info,
        request: ClearVariableMonitoringRequest,
    ): ClearVariableMonitoringResponse {
        stateStore.record("ClearVariableMonitoring", request)
        val results = request.id.map { id ->
            ClearMonitoringResult(id = id, status = ClearMonitoringStatusEnum.Accepted)
        }
        return ClearVariableMonitoringResponse(clearMonitoringResult = results)
    }

    override suspend fun customerInformation(
        ocppSessionInfo: OcppSession.Info,
        request: CustomerInformationRequest,
    ): CustomerInformationResponse {
        stateStore.record("CustomerInformation", request)
        return CustomerInformationResponse(status = CustomerInformationStatusEnum.Accepted)
    }

    override suspend fun getLog(
        ocppSessionInfo: OcppSession.Info,
        request: GetLogRequest,
    ): GetLogResponse {
        stateStore.record("GetLog", request)
        return GetLogResponse(status = LogStatusEnum.Accepted)
    }

    override suspend fun getMonitoringReport(
        ocppSessionInfo: OcppSession.Info,
        request: GetMonitoringReportRequest,
    ): GetMonitoringReportResponse {
        stateStore.record("GetMonitoringReport", request)
        return GetMonitoringReportResponse(status = GenericDeviceModelStatusEnum.Accepted)
    }

    override suspend fun setMonitoringBase(
        ocppSessionInfo: OcppSession.Info,
        request: SetMonitoringBaseRequest,
    ): SetMonitoringBaseResponse {
        stateStore.record("SetMonitoringBase", request)
        return SetMonitoringBaseResponse(status = GenericDeviceModelStatusEnum.Accepted)
    }

    override suspend fun setMonitoringLevel(
        ocppSessionInfo: OcppSession.Info,
        request: SetMonitoringLevelRequest,
    ): SetMonitoringLevelResponse {
        stateStore.record("SetMonitoringLevel", request)
        return SetMonitoringLevelResponse(status = GenericStatusEnum.Accepted)
    }

    override suspend fun setVariableMonitoring(
        ocppSessionInfo: OcppSession.Info,
        request: SetVariableMonitoringRequest,
    ): SetVariableMonitoringResponse {
        stateStore.record("SetVariableMonitoring", request)
        return SetVariableMonitoringResponse(setMonitoringResult = emptyList())
    }
}
