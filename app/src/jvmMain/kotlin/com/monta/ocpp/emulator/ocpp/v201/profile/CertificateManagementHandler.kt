package com.monta.ocpp.emulator.ocpp.v201.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v201.blocks.certificatemanagement.CertificateManagementClientDispatcher
import com.monta.library.ocpp.v201.blocks.certificatemanagement.CertificateSignedRequest
import com.monta.library.ocpp.v201.blocks.certificatemanagement.CertificateSignedResponse
import com.monta.library.ocpp.v201.blocks.certificatemanagement.DeleteCertificateRequest
import com.monta.library.ocpp.v201.blocks.certificatemanagement.DeleteCertificateResponse
import com.monta.library.ocpp.v201.blocks.certificatemanagement.GetInstalledCertificateIdsRequest
import com.monta.library.ocpp.v201.blocks.certificatemanagement.GetInstalledCertificateIdsResponse
import com.monta.library.ocpp.v201.blocks.certificatemanagement.InstallCertificateRequest
import com.monta.library.ocpp.v201.blocks.certificatemanagement.InstallCertificateResponse
import com.monta.library.ocpp.v201.blocks.certificatemanagement.common.CertificateHashData
import com.monta.library.ocpp.v201.blocks.certificatemanagement.common.GetCertificateIdUse
import com.monta.library.ocpp.v201.common.HashAlgorithm
import com.monta.ocpp.emulator.chargepoint.certificate.service.ChargePointCertificateService
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import javax.inject.Singleton

@Singleton
class CertificateManagementHandler(
    private val chargePointService: ChargePointService,
    private val certificateService: ChargePointCertificateService,
) : CertificateManagementClientDispatcher.Listener {
    override suspend fun certificateSigned(
        ocppSessionInfo: OcppSession.Info,
        request: CertificateSignedRequest,
    ): CertificateSignedResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        certificateService.storeSignedCertificate(chargePoint, request.certificateChain)
        return CertificateSignedResponse(status = CertificateSignedResponse.Status.Accepted)
    }

    override suspend fun deleteCertificate(
        ocppSessionInfo: OcppSession.Info,
        request: DeleteCertificateRequest,
    ): DeleteCertificateResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val deleted = certificateService.deleteByHashData(
            chargePoint = chargePoint,
            hashAlgorithm = request.certificateHashData.hashAlgorithm.name,
            issuerNameHash = request.certificateHashData.issuerNameHash,
            issuerKeyHash = request.certificateHashData.issuerKeyHash,
            serialNumber = request.certificateHashData.serialNumber,
        )
        return DeleteCertificateResponse(
            status = if (deleted) DeleteCertificateResponse.Status.Accepted else DeleteCertificateResponse.Status.NotFound,
        )
    }

    override suspend fun getInstalledCertificateIds(
        ocppSessionInfo: OcppSession.Info,
        request: GetInstalledCertificateIdsRequest,
    ): GetInstalledCertificateIdsResponse {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val certificates = certificateService.list(chargePoint)
        val chain = certificates.mapNotNull { certificate ->
            val issuerNameHash = certificate.issuerNameHash ?: return@mapNotNull null
            val issuerKeyHash = certificate.issuerKeyHash ?: return@mapNotNull null
            val serialNumber = certificate.serialNumber ?: return@mapNotNull null
            GetInstalledCertificateIdsResponse.CertificateHashDataChain(
                certificateHashData = CertificateHashData(
                    hashAlgorithm = runCatching { HashAlgorithm.valueOf(certificate.hashAlgorithm) }.getOrDefault(HashAlgorithm.SHA256),
                    issuerNameHash = issuerNameHash,
                    issuerKeyHash = issuerKeyHash,
                    serialNumber = serialNumber,
                ),
                certificateType = GetCertificateIdUse.ManufacturerRootCertificate,
            )
        }
        return GetInstalledCertificateIdsResponse(
            status = if (chain.isEmpty()) {
                GetInstalledCertificateIdsResponse.Status.NotFound
            } else {
                GetInstalledCertificateIdsResponse.Status.Accepted
            },
            certificateHashDataChain = chain.ifEmpty { null },
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
        return InstallCertificateResponse(status = InstallCertificateResponse.Status.Accepted)
    }
}
