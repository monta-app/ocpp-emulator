package com.monta.ocpp.emulator.chargepoint.certificate.service

import com.monta.ocpp.emulator.chargepoint.certificate.entity.ChargePointCertificateDAO
import com.monta.ocpp.emulator.chargepoint.certificate.repository.ChargePointCertificateRepository
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import java.security.MessageDigest
import javax.inject.Singleton

@Singleton
class ChargePointCertificateService(
    private val repository: ChargePointCertificateRepository,
) {

    fun install(
        chargePoint: ChargePointDAO,
        certificateType: String,
        pem: String,
        hashAlgorithm: String = "SHA256",
        issuerNameHash: String? = null,
        issuerKeyHash: String? = null,
        serialNumber: String? = null,
    ): ChargePointCertificateDAO {
        val certificateHash = sha256Hex(pem)
        repository.findByHash(chargePoint, certificateHash)?.let { return it }
        return repository.create(
            chargePoint = chargePoint,
            certificateType = certificateType,
            certificateHash = certificateHash,
            hashAlgorithm = hashAlgorithm,
            issuerNameHash = issuerNameHash,
            issuerKeyHash = issuerKeyHash,
            serialNumber = serialNumber,
            pem = pem,
        )
    }

    fun list(
        chargePoint: ChargePointDAO,
        certificateType: String? = null,
    ): List<ChargePointCertificateDAO> = repository.listByChargePoint(chargePoint, certificateType)

    fun deleteByHashData(
        chargePoint: ChargePointDAO,
        hashAlgorithm: String,
        issuerNameHash: String?,
        issuerKeyHash: String?,
        serialNumber: String?,
    ): Boolean {
        val match = repository.listByChargePoint(chargePoint).firstOrNull { cert ->
            cert.hashAlgorithm.equals(hashAlgorithm, ignoreCase = true) &&
                (issuerNameHash == null || cert.issuerNameHash == issuerNameHash) &&
                (issuerKeyHash == null || cert.issuerKeyHash == issuerKeyHash) &&
                (serialNumber == null || cert.serialNumber == serialNumber)
        } ?: return false
        repository.delete(match)
        return true
    }

    fun storeSignedCertificate(
        chargePoint: ChargePointDAO,
        certificateChain: String,
    ): ChargePointCertificateDAO {
        return install(
            chargePoint = chargePoint,
            certificateType = "ChargingStationCertificate",
            pem = certificateChain,
        )
    }

    private fun sha256Hex(
        value: String,
    ): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
