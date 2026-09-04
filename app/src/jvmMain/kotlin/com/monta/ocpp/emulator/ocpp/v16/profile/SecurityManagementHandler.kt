package com.monta.ocpp.emulator.ocpp.v16.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageRequest
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageRequestType
import com.monta.library.ocpp.v16.security.CertificateHashDataType
import com.monta.library.ocpp.v16.security.CertificateSignedConfirmation
import com.monta.library.ocpp.v16.security.CertificateSignedRequest
import com.monta.library.ocpp.v16.security.CertificateSignedStatusEnumType
import com.monta.library.ocpp.v16.security.CertificateStatusEnumType
import com.monta.library.ocpp.v16.security.DeleteCertificateConfirmation
import com.monta.library.ocpp.v16.security.DeleteCertificateRequest
import com.monta.library.ocpp.v16.security.DeleteCertificateStatusEnumType
import com.monta.library.ocpp.v16.security.ExtendedTriggerMessageConfirmation
import com.monta.library.ocpp.v16.security.ExtendedTriggerMessageRequest
import com.monta.library.ocpp.v16.security.GetInstalledCertificateIdsConfirmation
import com.monta.library.ocpp.v16.security.GetInstalledCertificateIdsRequest
import com.monta.library.ocpp.v16.security.GetInstalledCertificateStatusEnumType
import com.monta.library.ocpp.v16.security.GetLogConfirmation
import com.monta.library.ocpp.v16.security.GetLogRequest
import com.monta.library.ocpp.v16.security.HashAlgorithmEnumType
import com.monta.library.ocpp.v16.security.InstallCertificateConfirmation
import com.monta.library.ocpp.v16.security.InstallCertificateRequest
import com.monta.library.ocpp.v16.security.LogStatusEnumType
import com.monta.library.ocpp.v16.security.MessageTriggerEnumType
import com.monta.library.ocpp.v16.security.SecurityClientProfile
import com.monta.library.ocpp.v16.security.SignedUpdateFirmwareConfirmation
import com.monta.library.ocpp.v16.security.SignedUpdateFirmwareRequest
import com.monta.library.ocpp.v16.security.TriggerMessageStatusEnumType
import com.monta.library.ocpp.v16.security.UpdateFirmwareStatusEnumType
import com.monta.library.ocpp.v16.security.UploadLogStatus
import com.monta.ocpp.emulator.chargepoint.certificate.service.ChargePointCertificateService
import com.monta.ocpp.emulator.chargepoint.core.service.ChargePointService
import com.monta.ocpp.emulator.ocpp.v16.service.ChargePointManager
import com.monta.ocpp.emulator.platform.logging.service.GlobalLogger
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.platform.util.launchThread
import javax.inject.Singleton

@Singleton
class SecurityManagementHandler(
    private val firmwareManagementHandler: FirmwareManagementHandler,
    private val certificateService: ChargePointCertificateService,
    private val triggerMessageHandler: TriggerMessageHandler,
) : SecurityClientProfile.Listener {

    private val chargePointService: ChargePointService by injectAnywhere()
    private val chargePointManager: ChargePointManager by injectAnywhere()

    override suspend fun certificateSigned(
        ocppSessionInfo: OcppSession.Info,
        request: CertificateSignedRequest,
    ): CertificateSignedConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        if (request.certificateChain.isBlank()) {
            return CertificateSignedConfirmation(status = CertificateSignedStatusEnumType.Rejected)
        }
        certificateService.storeSignedCertificate(chargePoint, request.certificateChain)
        GlobalLogger.info(chargePoint, "CertificateSigned accepted")
        return CertificateSignedConfirmation(status = CertificateSignedStatusEnumType.Accepted)
    }

    override suspend fun deleteCertificate(
        ocppSessionInfo: OcppSession.Info,
        request: DeleteCertificateRequest,
    ): DeleteCertificateConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val hash = request.certificateHashData
        val deleted = certificateService.deleteByHashData(
            chargePoint = chargePoint,
            hashAlgorithm = hash.hashAlgorithm.name,
            issuerNameHash = hash.issuerNameHash,
            issuerKeyHash = hash.issuerKeyHash,
            serialNumber = hash.serialNumber,
        )
        return DeleteCertificateConfirmation(
            status = if (deleted) {
                DeleteCertificateStatusEnumType.Accepted
            } else {
                DeleteCertificateStatusEnumType.NotFound
            },
        )
    }

    override suspend fun extendedTriggerMessage(
        ocppSessionInfo: OcppSession.Info,
        request: ExtendedTriggerMessageRequest,
    ): ExtendedTriggerMessageConfirmation {
        val mapped = when (request.requestedMessage) {
            MessageTriggerEnumType.BootNotification -> TriggerMessageRequestType.BootNotification
            MessageTriggerEnumType.Heartbeat -> TriggerMessageRequestType.Heartbeat
            MessageTriggerEnumType.MeterValues -> TriggerMessageRequestType.MeterValues
            MessageTriggerEnumType.StatusNotification -> TriggerMessageRequestType.StatusNotification
            MessageTriggerEnumType.FirmwareStatusNotification -> TriggerMessageRequestType.FirmwareStatusNotification
            MessageTriggerEnumType.LogStatusNotification -> null
            MessageTriggerEnumType.SignChargePointCertificate -> null
        }

        if (mapped != null) {
            triggerMessageHandler.triggerMessage(
                ocppSessionInfo = ocppSessionInfo,
                request = TriggerMessageRequest(
                    requestedMessage = mapped,
                    connectorId = request.connectorId,
                ),
            )
            return ExtendedTriggerMessageConfirmation(status = TriggerMessageStatusEnumType.Accepted)
        }

        if (request.requestedMessage == MessageTriggerEnumType.LogStatusNotification) {
            launchThread {
                val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
                chargePointManager.logStatusNotification(
                    chargePoint = chargePoint,
                    status = UploadLogStatus.Idle,
                    requestId = 0,
                )
            }
            return ExtendedTriggerMessageConfirmation(status = TriggerMessageStatusEnumType.Accepted)
        }

        if (request.requestedMessage == MessageTriggerEnumType.SignChargePointCertificate) {
            launchThread {
                val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
                chargePointManager.signCertificate(chargePoint)
            }
            return ExtendedTriggerMessageConfirmation(status = TriggerMessageStatusEnumType.Accepted)
        }

        return ExtendedTriggerMessageConfirmation(status = TriggerMessageStatusEnumType.NotImplemented)
    }

    override suspend fun getInstalledCertificateIds(
        ocppSessionInfo: OcppSession.Info,
        request: GetInstalledCertificateIdsRequest,
    ): GetInstalledCertificateIdsConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val certificates = certificateService.list(
            chargePoint = chargePoint,
            certificateType = request.certificateType.name,
        )
        if (certificates.isEmpty()) {
            return GetInstalledCertificateIdsConfirmation(
                status = GetInstalledCertificateStatusEnumType.NotFound,
            )
        }
        return GetInstalledCertificateIdsConfirmation(
            status = GetInstalledCertificateStatusEnumType.Accepted,
            certificateHashData = certificates.map { cert ->
                CertificateHashDataType(
                    hashAlgorithm = runCatching {
                        HashAlgorithmEnumType.valueOf(cert.hashAlgorithm.uppercase())
                    }.getOrDefault(HashAlgorithmEnumType.SHA256),
                    issuerNameHash = cert.issuerNameHash ?: cert.certificateHash,
                    issuerKeyHash = cert.issuerKeyHash ?: cert.certificateHash,
                    serialNumber = cert.serialNumber ?: cert.certificateHash.take(8).uppercase(),
                )
            },
        )
    }

    override suspend fun getLog(
        ocppSessionInfo: OcppSession.Info,
        request: GetLogRequest,
    ): GetLogConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        val fileName = "ocpp-${request.logType.name.lowercase()}-${System.currentTimeMillis()}.log"
        launchThread {
            chargePointManager.startLogUpload(
                chargePoint = chargePoint,
                remoteLocation = request.log.remoteLocation,
                fileName = fileName,
                requestId = request.requestId,
            )
        }
        return GetLogConfirmation(
            status = LogStatusEnumType.Accepted,
            filename = fileName,
        )
    }

    override suspend fun installCertificate(
        ocppSessionInfo: OcppSession.Info,
        request: InstallCertificateRequest,
    ): InstallCertificateConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        if (request.certificate.isBlank()) {
            return InstallCertificateConfirmation(status = CertificateStatusEnumType.Rejected)
        }
        certificateService.install(
            chargePoint = chargePoint,
            certificateType = request.certificateType.name,
            pem = request.certificate,
        )
        GlobalLogger.info(chargePoint, "InstallCertificate ${request.certificateType} accepted")
        return InstallCertificateConfirmation(status = CertificateStatusEnumType.Accepted)
    }

    override suspend fun signedUpdateFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: SignedUpdateFirmwareRequest,
    ): SignedUpdateFirmwareConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        try {
            return SignedUpdateFirmwareConfirmation(
                UpdateFirmwareStatusEnumType.Accepted,
            )
        } finally {
            firmwareManagementHandler.startFirmwareUpdate(
                chargePoint = chargePoint,
                location = request.firmware.location,
            )
        }
    }
}
