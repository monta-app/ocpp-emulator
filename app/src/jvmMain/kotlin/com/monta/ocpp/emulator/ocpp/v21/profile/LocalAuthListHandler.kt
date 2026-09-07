package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.localauthorizationlistmanagement.LocalAuthorizationListManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearCacheRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearCacheResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLocalListVersionRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLocalListVersionResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SendLocalListRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SendLocalListResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ClearCacheStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.SendLocalListStatusEnum
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.inject.Singleton

@Singleton
class LocalAuthListHandler : LocalAuthorizationListManagementClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()

    override suspend fun clearCache(
        ocppSessionInfo: OcppSession.Info,
        request: ClearCacheRequest,
    ): ClearCacheResponse {
        return ClearCacheResponse(status = ClearCacheStatusEnum.Accepted)
    }

    override suspend fun getLocalListVersion(
        ocppSessionInfo: OcppSession.Info,
        request: GetLocalListVersionRequest,
    ): GetLocalListVersionResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        return GetLocalListVersionResponse(versionNumber = chargePoint.localAuthList.version)
    }

    override suspend fun sendLocalList(
        ocppSessionInfo: OcppSession.Info,
        request: SendLocalListRequest,
    ): SendLocalListResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        transaction {
            chargePoint.localAuthList.version = request.versionNumber
        }
        return SendLocalListResponse(status = SendLocalListStatusEnum.Accepted)
    }
}
