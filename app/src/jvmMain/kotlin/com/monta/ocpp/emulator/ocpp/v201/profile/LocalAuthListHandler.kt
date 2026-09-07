package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.localauthorizationlistmanagement.ClearCacheRequest
import com.monta.library.ocpp.v201.blocks.localauthorizationlistmanagement.ClearCacheResponse
import com.monta.library.ocpp.v201.blocks.localauthorizationlistmanagement.GetLocalListVersionRequest
import com.monta.library.ocpp.v201.blocks.localauthorizationlistmanagement.GetLocalListVersionResponse
import com.monta.library.ocpp.v201.blocks.localauthorizationlistmanagement.LocalAuthorizationListManagementClientDispatcher
import com.monta.library.ocpp.v201.blocks.localauthorizationlistmanagement.SendLocalListRequest
import com.monta.library.ocpp.v201.blocks.localauthorizationlistmanagement.SendLocalListResponse
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.inject.Singleton

@Singleton
class LocalAuthListHandler(
    private val chargePointService: ChargePointService,
) : LocalAuthorizationListManagementClientDispatcher.Listener {
    override suspend fun clearCache(
        ocppSessionInfo: OcppSession.Info,
        request: ClearCacheRequest,
    ): ClearCacheResponse {
        chargePointService.getByIdentity(ocppSessionInfo.identity)
        return ClearCacheResponse(status = ClearCacheResponse.Status.Accepted)
    }

    override suspend fun getLocalListVersion(
        ocppSessionInfo: OcppSession.Info,
        request: GetLocalListVersionRequest,
    ): GetLocalListVersionResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        return GetLocalListVersionResponse(versionNumber = chargePoint.localAuthList.version.toLong())
    }

    override suspend fun sendLocalList(
        ocppSessionInfo: OcppSession.Info,
        request: SendLocalListRequest,
    ): SendLocalListResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        transaction {
            chargePoint.localAuthList.version = request.versionNumber.toInt()
        }
        return SendLocalListResponse(status = SendLocalListResponse.Status.Accepted)
    }
}
