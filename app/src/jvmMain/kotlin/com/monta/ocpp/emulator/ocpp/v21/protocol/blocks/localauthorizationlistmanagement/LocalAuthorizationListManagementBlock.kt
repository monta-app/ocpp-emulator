// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.localauthorizationlistmanagement

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearCacheFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearCacheRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearCacheResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLocalListVersionFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLocalListVersionRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetLocalListVersionResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SendLocalListFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SendLocalListRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SendLocalListResponse

val localauthorizationlistmanagementFeatures = listOf(
    ClearCacheFeature,
    GetLocalListVersionFeature,
    SendLocalListFeature,
)

class LocalAuthorizationListManagementClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = localauthorizationlistmanagementFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is ClearCacheRequest -> listener.clearCache(ocppSessionInfo, request)
            is GetLocalListVersionRequest -> listener.getLocalListVersion(ocppSessionInfo, request)
            is SendLocalListRequest -> listener.sendLocalList(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun clearCache(
            ocppSessionInfo: OcppSession.Info,
            request: ClearCacheRequest,
        ): ClearCacheResponse

        suspend fun getLocalListVersion(
            ocppSessionInfo: OcppSession.Info,
            request: GetLocalListVersionRequest,
        ): GetLocalListVersionResponse

        suspend fun sendLocalList(
            ocppSessionInfo: OcppSession.Info,
            request: SendLocalListRequest,
        ): SendLocalListResponse
    }
}
