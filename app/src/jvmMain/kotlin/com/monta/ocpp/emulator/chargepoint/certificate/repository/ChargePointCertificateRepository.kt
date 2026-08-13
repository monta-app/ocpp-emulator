package com.monta.ocpp.emulator.chargepoint.certificate.repository

import com.monta.ocpp.emulator.chargepoint.certificate.entity.ChargePointCertificateDAO
import com.monta.ocpp.emulator.chargepoint.certificate.entity.ChargePointCertificateTable
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.inject.Singleton

@Singleton
class ChargePointCertificateRepository {

    fun listByChargePoint(
        chargePoint: ChargePointDAO,
        certificateType: String? = null,
    ): List<ChargePointCertificateDAO> = transaction {
        if (certificateType == null) {
            ChargePointCertificateDAO.find {
                ChargePointCertificateTable.chargePointId eq chargePoint.id
            }.toList()
        } else {
            ChargePointCertificateDAO.find {
                (ChargePointCertificateTable.chargePointId eq chargePoint.id) and
                    (ChargePointCertificateTable.certificateType eq certificateType)
            }.toList()
        }
    }

    fun findByHash(
        chargePoint: ChargePointDAO,
        certificateHash: String,
    ): ChargePointCertificateDAO? = transaction {
        ChargePointCertificateDAO.find {
            (ChargePointCertificateTable.chargePointId eq chargePoint.id) and
                (ChargePointCertificateTable.certificateHash eq certificateHash)
        }.firstOrNull()
    }

    fun create(
        chargePoint: ChargePointDAO,
        certificateType: String,
        certificateHash: String,
        hashAlgorithm: String,
        issuerNameHash: String?,
        issuerKeyHash: String?,
        serialNumber: String?,
        pem: String,
    ): ChargePointCertificateDAO = transaction {
        ChargePointCertificateDAO.newInstance(
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

    fun delete(
        certificate: ChargePointCertificateDAO,
    ) = transaction {
        certificate.delete()
    }
}
