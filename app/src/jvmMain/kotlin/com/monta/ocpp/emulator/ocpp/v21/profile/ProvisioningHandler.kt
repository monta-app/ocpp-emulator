package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.devicemodel.service.DeviceModelService
import com.monta.ocpp.emulator.chargepoint.devicemodel.service.DeviceVariable
import com.monta.ocpp.emulator.ocpp.v21.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.provisioning.ProvisioningClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetBaseReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetBaseReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetReportRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetReportResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetVariablesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetVariablesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ResetRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ResetResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetNetworkProfileRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetNetworkProfileResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariablesRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetVariablesResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GenericDeviceModelStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetVariableResult
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetVariableStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ResetStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SetNetworkProfileStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SetVariableResult
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SetVariableStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.service.ChargePointManager
import com.monta.ocpp.emulator.ocpp.v21.service.Ocpp21StateStore
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.platform.util.launchThread
import javax.inject.Singleton

@Singleton
class ProvisioningHandler : ProvisioningClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val deviceModelService: DeviceModelService by injectAnywhere()
    private val chargePointManager: ChargePointManager by injectAnywhere()
    private val connectionManager: ConnectionManager by injectAnywhere()
    private val stateStore: Ocpp21StateStore by injectAnywhere()

    override suspend fun getBaseReport(
        ocppSessionInfo: OcppSession.Info,
        request: GetBaseReportRequest,
    ): GetBaseReportResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("GetBaseReport", request)
        launchThread {
            chargePointManager.sendBaseReport(chargePoint, request.requestId)
        }
        return GetBaseReportResponse(status = GenericDeviceModelStatusEnum.Accepted)
    }

    override suspend fun getReport(
        ocppSessionInfo: OcppSession.Info,
        request: GetReportRequest,
    ): GetReportResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("GetReport", request)
        launchThread {
            chargePointManager.sendBaseReport(chargePoint, request.requestId)
        }
        return GetReportResponse(status = GenericDeviceModelStatusEnum.Accepted)
    }

    override suspend fun getVariables(
        ocppSessionInfo: OcppSession.Info,
        request: GetVariablesRequest,
    ): GetVariablesResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("GetVariables", request)
        val results = request.getVariableData.map { data ->
            val found = deviceModelService.get(
                chargePointId = chargePoint.idValue,
                componentName = data.component.name,
                variableName = data.variable.name,
                componentInstance = data.component.instance,
                variableInstance = data.variable.instance,
            )
            if (found == null) {
                GetVariableResult(
                    attributeStatus = GetVariableStatusEnum.UnknownVariable,
                    component = data.component,
                    variable = data.variable,
                    attributeType = data.attributeType,
                )
            } else {
                GetVariableResult(
                    attributeStatus = GetVariableStatusEnum.Accepted,
                    component = data.component,
                    variable = data.variable,
                    attributeType = data.attributeType,
                    attributeValue = found.value,
                )
            }
        }
        return GetVariablesResponse(getVariableResult = results)
    }

    override suspend fun reset(
        ocppSessionInfo: OcppSession.Info,
        request: ResetRequest,
    ): ResetResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("Reset", request)
        launchThread {
            connectionManager.reconnect(chargePoint.idValue, 2)
        }
        return ResetResponse(status = ResetStatusEnum.Accepted)
    }

    override suspend fun setNetworkProfile(
        ocppSessionInfo: OcppSession.Info,
        request: SetNetworkProfileRequest,
    ): SetNetworkProfileResponse {
        stateStore.record("SetNetworkProfile", request)
        return SetNetworkProfileResponse(status = SetNetworkProfileStatusEnum.Accepted)
    }

    override suspend fun setVariables(
        ocppSessionInfo: OcppSession.Info,
        request: SetVariablesRequest,
    ): SetVariablesResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        stateStore.record("SetVariables", request)
        val results = request.setVariableData.map { data ->
            val ok = deviceModelService.set(
                chargePointId = chargePoint.idValue,
                variable = DeviceVariable(
                    componentName = data.component.name,
                    componentInstance = data.component.instance,
                    variableName = data.variable.name,
                    variableInstance = data.variable.instance,
                    value = data.attributeValue,
                    readonly = false,
                ),
            )
            SetVariableResult(
                attributeStatus = if (ok) SetVariableStatusEnum.Accepted else SetVariableStatusEnum.Rejected,
                component = data.component,
                variable = data.variable,
                attributeType = data.attributeType,
            )
        }
        return SetVariablesResponse(setVariableResult = results)
    }
}
