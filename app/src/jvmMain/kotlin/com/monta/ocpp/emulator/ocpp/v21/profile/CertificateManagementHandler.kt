package com.monta.ocpp.emulator.ocpp.v21.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.ocpp.emulator.chargepoint.certificate.service.ChargePointCertificateService
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.certificatemanagement.CertificateManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CertificateSignedRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.CertificateSignedResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DeleteCertificateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.DeleteCertificateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetInstalledCertificateIdsRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetInstalledCertificateIdsResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.InstallCertificateRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.InstallCertificateResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateHashData
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateHashDataChain
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.CertificateSignedStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.DeleteCertificateStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetCertificateIdUseEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.GetInstalledCertificateStatusEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.HashAlgorithmEnum
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.InstallCertificateStatusEnum
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import javax.inject.Singleton

@Singleton
class CertificateManagementHandler : CertificateManagementClientDispatcher.Listener {
    private val chargePointService: ChargePointService by injectAnywhere()
    private val certificateService: ChargePointCertificateService by injectAnywhere()

    override suspend fun certificateSigned(
        ocppSessionInfo: OcppSession.Info,
        request: CertificateSignedRequest,
    ): CertificateSignedResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        certificateService.storeSignedCertificate(chargePoint, request.certificateChain)
        return CertificateSignedResponse(status = CertificateSignedStatusEnum.Accepted)
    }

    override suspend fun deleteCertificate(
        ocppSessionInfo: OcppSession.Info,
        request: DeleteCertificateRequest,
    ): DeleteCertificateResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val hash = request.certificateHashData
        val deleted = certificateService.deleteByHashData(
            chargePoint = chargePoint,
            hashAlgorithm = hash.hashAlgorithm.name,
            issuerNameHash = hash.issuerNameHash,
            issuerKeyHash = hash.issuerKeyHash,
            serialNumber = hash.serialNumber,
        )
        return DeleteCertificateResponse(
            status = if (deleted) {
                DeleteCertificateStatusEnum.Accepted
            } else {
                DeleteCertificateStatusEnum.NotFound
            },
        )
    }

    override suspend fun getInstalledCertificateIds(
        ocppSessionInfo: OcppSession.Info,
        request: GetInstalledCertificateIdsRequest,
    ): GetInstalledCertificateIdsResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val typeFilter = request.certificateType?.map { it.name }?.toSet()
        val certificates = certificateService.list(chargePoint).filter { cert ->
            typeFilter == null || cert.certificateType in typeFilter
        }
        val chain = certificates.mapNotNull { cert ->
            val certType = runCatching { GetCertificateIdUseEnum.valueOf(cert.certificateType) }.getOrNull()
                ?: return@mapNotNull null
            CertificateHashDataChain(
                certificateType = certType,
                certificateHashData = CertificateHashData(
                    hashAlgorithm = HashAlgorithmEnum.SHA256,
                    issuerNameHash = cert.issuerNameHash.orEmpty(),
                    issuerKeyHash = cert.issuerKeyHash.orEmpty(),
                    serialNumber = cert.serialNumber.orEmpty(),
                ),
            )
        }
        return GetInstalledCertificateIdsResponse(
            status = GetInstalledCertificateStatusEnum.Accepted,
            certificateHashDataChain = chain,
        )
    }

    override suspend fun installCertificate(
        ocppSessionInfo: OcppSession.Info,
        request: InstallCertificateRequest,
    ): InstallCertificateResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        certificateService.install(
            chargePoint = chargePoint,
            certificateType = request.certificateType.name,
            pem = request.certificate,
        )
        return InstallCertificateResponse(status = InstallCertificateStatusEnum.Accepted)
    }
}
