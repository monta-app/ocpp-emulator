package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.provisioning.GetBaseReportRequest
import com.monta.library.ocpp.v201.blocks.provisioning.GetBaseReportResponse
import com.monta.library.ocpp.v201.blocks.provisioning.GetReportRequest
import com.monta.library.ocpp.v201.blocks.provisioning.GetReportResponse
import com.monta.library.ocpp.v201.blocks.provisioning.GetVariablesRequest
import com.monta.library.ocpp.v201.blocks.provisioning.GetVariablesResponse
import com.monta.library.ocpp.v201.blocks.provisioning.ProvisioningClientDispatcher
import com.monta.library.ocpp.v201.blocks.provisioning.ResetRequest
import com.monta.library.ocpp.v201.blocks.provisioning.ResetResponse
import com.monta.library.ocpp.v201.blocks.provisioning.SetNetworkProfileRequest
import com.monta.library.ocpp.v201.blocks.provisioning.SetNetworkProfileResponse
import com.monta.library.ocpp.v201.blocks.provisioning.SetVariablesRequest
import com.monta.library.ocpp.v201.blocks.provisioning.SetVariablesResponse
import com.monta.library.ocpp.v201.common.AttributeType
import com.monta.library.ocpp.v201.common.GenericDeviceModelStatus
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.chargepoint.devicemodel.service.DeviceModelService
import com.monta.ocpp.emulator.chargepoint.devicemodel.service.DeviceVariable
import com.monta.ocpp.emulator.ocpp.v201.connection.ConnectionManager
import com.monta.ocpp.emulator.ocpp.v201.service.ChargePointManager
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.launchThread
import kotlinx.coroutines.delay
import javax.inject.Singleton

@Singleton
class ProvisioningHandler(
    private val chargePointService: ChargePointService,
    private val chargePointManager: ChargePointManager,
    private val deviceModelService: DeviceModelService,
    private val connectionManager: ConnectionManager,
) : ProvisioningClientDispatcher.Listener {
    override suspend fun getBaseReport(
        ocppSessionInfo: OcppSession.Info,
        request: GetBaseReportRequest,
    ): GetBaseReportResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        launchThread { chargePointManager.sendBaseReport(chargePoint, request.requestId) }
        return GetBaseReportResponse(status = GetBaseReportResponse.Status.Accepted)
    }

    override suspend fun getReport(
        ocppSessionInfo: OcppSession.Info,
        request: GetReportRequest,
    ): GetReportResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        launchThread { chargePointManager.sendBaseReport(chargePoint, request.requestId) }
        return GetReportResponse(status = GenericDeviceModelStatus.Accepted)
    }

    override suspend fun getVariables(
        ocppSessionInfo: OcppSession.Info,
        request: GetVariablesRequest,
    ): GetVariablesResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        deviceModelService.seedDefaults(
            chargePoint.idValue,
            chargePoint.brand,
            chargePoint.model,
            chargePoint.serial,
            chargePoint.firmware,
        )
        val results = request.getVariableData.map { data ->
            val found = deviceModelService.get(
                chargePoint.idValue,
                data.component.name,
                data.variable.name,
                data.component.instance,
                data.variable.instance,
            )
            GetVariablesResponse.GetVariableResult(
                attributeStatus = if (found != null) {
                    GetVariablesResponse.AttributeStatus.Accepted
                } else {
                    GetVariablesResponse.AttributeStatus.UnknownVariable
                },
                component = data.component,
                variable = data.variable,
                attributeType = data.attributeType ?: AttributeType.Actual,
                attributeValue = found?.value,
            )
        }
        return GetVariablesResponse(getVariableResult = results)
    }

    override suspend fun reset(
        ocppSessionInfo: OcppSession.Info,
        request: ResetRequest,
    ): ResetResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        launchThread {
            delay(500)
            connectionManager.reconnect(chargePoint.idValue, 1)
        }
        return ResetResponse(status = ResetResponse.Status.Accepted)
    }

    override suspend fun setNetworkProfile(
        ocppSessionInfo: OcppSession.Info,
        request: SetNetworkProfileRequest,
    ): SetNetworkProfileResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return SetNetworkProfileResponse(status = SetNetworkProfileResponse.Status.Accepted)
    }

    override suspend fun setVariables(
        ocppSessionInfo: OcppSession.Info,
        request: SetVariablesRequest,
    ): SetVariablesResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val results = request.setVariableData.map { data ->
            val ok = deviceModelService.set(
                chargePoint.idValue,
                DeviceVariable(
                    componentName = data.component.name,
                    componentInstance = data.component.instance,
                    variableName = data.variable.name,
                    variableInstance = data.variable.instance,
                    value = data.attributeValue,
                ),
            )
            SetVariablesResponse.SetVariableResult(
                attributeStatus = if (ok) {
                    SetVariablesResponse.AttributeStatus.Accepted
                } else {
                    SetVariablesResponse.AttributeStatus.Rejected
                },
                component = data.component,
                variable = data.variable,
                attributeType = data.attributeType,
            )
        }
        return SetVariablesResponse(setVariableResult = results)
    }
}
