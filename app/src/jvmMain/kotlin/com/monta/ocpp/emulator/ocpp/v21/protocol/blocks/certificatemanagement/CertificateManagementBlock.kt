// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.certificatemanagement

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CertificateSignedFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CertificateSignedRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CertificateSignedResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DeleteCertificateFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DeleteCertificateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DeleteCertificateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.Get15118EVCertificateFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.Get15118EVCertificateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.Get15118EVCertificateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateChainStatusFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateChainStatusRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateChainStatusResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateStatusFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateStatusRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetCertificateStatusResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetInstalledCertificateIdsFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetInstalledCertificateIdsRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetInstalledCertificateIdsResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.InstallCertificateFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.InstallCertificateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.InstallCertificateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SignCertificateFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SignCertificateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SignCertificateResponse

val certificatemanagementFeatures = listOf(
    Get15118EVCertificateFeature,
    GetCertificateStatusFeature,
    GetCertificateChainStatusFeature,
    SignCertificateFeature,
    CertificateSignedFeature,
    DeleteCertificateFeature,
    GetInstalledCertificateIdsFeature,
    InstallCertificateFeature,
)

class CertificateManagementClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = certificatemanagementFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is CertificateSignedRequest -> listener.certificateSigned(ocppSessionInfo, request)
            is DeleteCertificateRequest -> listener.deleteCertificate(ocppSessionInfo, request)
            is GetInstalledCertificateIdsRequest -> listener.getInstalledCertificateIds(ocppSessionInfo, request)
            is InstallCertificateRequest -> listener.installCertificate(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun certificateSigned(
            ocppSessionInfo: OcppSession.Info,
            request: CertificateSignedRequest,
        ): CertificateSignedResponse

        suspend fun deleteCertificate(
            ocppSessionInfo: OcppSession.Info,
            request: DeleteCertificateRequest,
        ): DeleteCertificateResponse

        suspend fun getInstalledCertificateIds(
            ocppSessionInfo: OcppSession.Info,
            request: GetInstalledCertificateIdsRequest,
        ): GetInstalledCertificateIdsResponse

        suspend fun installCertificate(
            ocppSessionInfo: OcppSession.Info,
            request: InstallCertificateRequest,
        ): InstallCertificateResponse
    }

    interface Sender {
        suspend fun get15118EVCertificate(
            request: Get15118EVCertificateRequest,
        ): Get15118EVCertificateResponse

        suspend fun getCertificateStatus(
            request: GetCertificateStatusRequest,
        ): GetCertificateStatusResponse

        suspend fun getCertificateChainStatus(
            request: GetCertificateChainStatusRequest,
        ): GetCertificateChainStatusResponse

        suspend fun signCertificate(
            request: SignCertificateRequest,
        ): SignCertificateResponse
    }
}
