package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.diagnostics.ClearVariableMonitoringRequest
import com.monta.library.ocpp.v201.blocks.diagnostics.ClearVariableMonitoringResponse
import com.monta.library.ocpp.v201.blocks.diagnostics.CustomerInformationRequest
import com.monta.library.ocpp.v201.blocks.diagnostics.CustomerInformationResponse
import com.monta.library.ocpp.v201.blocks.diagnostics.DiagnosticsClientDispatcher
import com.monta.library.ocpp.v201.blocks.diagnostics.GetLogRequest
import com.monta.library.ocpp.v201.blocks.diagnostics.GetLogResponse
import com.monta.library.ocpp.v201.blocks.diagnostics.GetMonitoringReportRequest
import com.monta.library.ocpp.v201.blocks.diagnostics.GetMonitoringReportResponse
import com.monta.library.ocpp.v201.blocks.diagnostics.SetMonitoringBaseRequest
import com.monta.library.ocpp.v201.blocks.diagnostics.SetMonitoringBaseResponse
import com.monta.library.ocpp.v201.blocks.diagnostics.SetMonitoringLevelRequest
import com.monta.library.ocpp.v201.blocks.diagnostics.SetMonitoringLevelResponse
import com.monta.library.ocpp.v201.blocks.diagnostics.SetVariableMonitoringRequest
import com.monta.library.ocpp.v201.blocks.diagnostics.SetVariableMonitoringResponse
import com.monta.library.ocpp.v201.common.GenericDeviceModelStatus
import com.monta.library.ocpp.v201.common.GenericStatus
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import javax.inject.Singleton

@Singleton
class DiagnosticsHandler(
    private val chargePointService: ChargePointService,
) : DiagnosticsClientDispatcher.Listener {
    override suspend fun clearVariableMonitoring(
        ocppSessionInfo: OcppSession.Info,
        request: ClearVariableMonitoringRequest,
    ): ClearVariableMonitoringResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return ClearVariableMonitoringResponse(
            clearMonitoringResult = request.id.map { id ->
                ClearVariableMonitoringResponse.ClearMonitoringResult(
                    status = ClearVariableMonitoringResponse.Status.Accepted,
                    id = id,
                )
            },
        )
    }

    override suspend fun customerInformation(
        ocppSessionInfo: OcppSession.Info,
        request: CustomerInformationRequest,
    ): CustomerInformationResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return CustomerInformationResponse(status = CustomerInformationResponse.Status.Accepted)
    }

    override suspend fun getLog(
        ocppSessionInfo: OcppSession.Info,
        request: GetLogRequest,
    ): GetLogResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return GetLogResponse(status = GetLogResponse.Status.Accepted, filename = "diagnostics.log")
    }

    override suspend fun getMonitoringReport(
        ocppSessionInfo: OcppSession.Info,
        request: GetMonitoringReportRequest,
    ): GetMonitoringReportResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return GetMonitoringReportResponse(status = GenericDeviceModelStatus.Accepted)
    }

    override suspend fun setMonitoringBase(
        ocppSessionInfo: OcppSession.Info,
        request: SetMonitoringBaseRequest,
    ): SetMonitoringBaseResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return SetMonitoringBaseResponse(status = GenericDeviceModelStatus.Accepted)
    }

    override suspend fun setMonitoringLevel(
        ocppSessionInfo: OcppSession.Info,
        request: SetMonitoringLevelRequest,
    ): SetMonitoringLevelResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return SetMonitoringLevelResponse(status = GenericStatus.Accepted)
    }

    override suspend fun setVariableMonitoring(
        ocppSessionInfo: OcppSession.Info,
        request: SetVariableMonitoringRequest,
    ): SetVariableMonitoringResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return SetVariableMonitoringResponse(
            setMonitoringResult = request.setMonitoringData.mapIndexed { index, data ->
                SetVariableMonitoringResponse.SetMonitoringResult(
                    id = index.toLong() + 1,
                    status = SetVariableMonitoringResponse.Status.Accepted,
                    type = SetVariableMonitoringResponse.Monitor.valueOf(data.type.name),
                    component = data.component,
                    variable = data.variable,
                    severity = data.severity,
                )
            },
        )
    }
}
