package com.monta.ocpp.emulator.v16.service.ocpp.profile

import com.monta.library.ocpp.common.session.OcppSession
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
import com.monta.library.ocpp.v16.security.InstallCertificateConfirmation
import com.monta.library.ocpp.v16.security.InstallCertificateRequest
import com.monta.library.ocpp.v16.security.LogStatusEnumType
import com.monta.library.ocpp.v16.security.SecurityClientProfile
import com.monta.library.ocpp.v16.security.SignedUpdateFirmwareConfirmation
import com.monta.library.ocpp.v16.security.SignedUpdateFirmwareRequest
import com.monta.library.ocpp.v16.security.TriggerMessageStatusEnumType
import com.monta.library.ocpp.v16.security.UpdateFirmwareStatusEnumType
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.v16.data.service.ChargePointService
import org.koin.core.annotation.Singleton

@Singleton
class SecurityManagementHandler(
    private val firmwareManagementHandler: FirmwareManagementHandler
) : SecurityClientProfile.Listener {

    private val chargePointService: ChargePointService by injectAnywhere()

    override suspend fun certificateSigned(
        ocppSessionInfo: OcppSession.Info,
        request: CertificateSignedRequest
    ): CertificateSignedConfirmation {
        return CertificateSignedConfirmation(
            status = CertificateSignedStatusEnumType.Rejected
        )
    }

    override suspend fun deleteCertificate(
        ocppSessionInfo: OcppSession.Info,
        request: DeleteCertificateRequest
    ): DeleteCertificateConfirmation {
        return DeleteCertificateConfirmation(
            status = DeleteCertificateStatusEnumType.NotFound
        )
    }

    override suspend fun extendedTriggerMessage(
        ocppSessionInfo: OcppSession.Info,
        request: ExtendedTriggerMessageRequest
    ): ExtendedTriggerMessageConfirmation {
        return ExtendedTriggerMessageConfirmation(
            status = TriggerMessageStatusEnumType.NotImplemented
        )
    }

    override suspend fun getInstalledCertificateIds(
        ocppSessionInfo: OcppSession.Info,
        request: GetInstalledCertificateIdsRequest
    ): GetInstalledCertificateIdsConfirmation {
        return GetInstalledCertificateIdsConfirmation(
            status = GetInstalledCertificateStatusEnumType.NotFound
        )
    }

    override suspend fun getLog(ocppSessionInfo: OcppSession.Info, request: GetLogRequest): GetLogConfirmation {
        return GetLogConfirmation(
            status = LogStatusEnumType.Rejected
        )
    }

    override suspend fun installCertificate(
        ocppSessionInfo: OcppSession.Info,
        request: InstallCertificateRequest
    ): InstallCertificateConfirmation {
        return InstallCertificateConfirmation(
            status = CertificateStatusEnumType.Rejected
        )
    }

    override suspend fun signedUpdateFirmware(
        ocppSessionInfo: OcppSession.Info,
        request: SignedUpdateFirmwareRequest
    ): SignedUpdateFirmwareConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        try {
            return SignedUpdateFirmwareConfirmation(
                UpdateFirmwareStatusEnumType.Accepted
            )
        } finally {
            firmwareManagementHandler.startFirmwareUpdate(
                chargePoint = chargePoint,
                location = request.firmware.location
            )
        }
    }
}
