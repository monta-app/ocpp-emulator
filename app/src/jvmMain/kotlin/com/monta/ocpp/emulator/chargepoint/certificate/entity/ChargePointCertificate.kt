package com.monta.ocpp.emulator.chargepoint.certificate.entity

import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.timestamp
import java.time.Instant

object ChargePointCertificateTable : LongIdTable("charge_point_certificate") {
    val chargePointId = reference("charge_point_id", ChargePointTable)
    val certificateType = varchar("certificate_type", 64)
    val certificateHash = varchar("certificate_hash", 128)
    val hashAlgorithm = varchar("hash_algorithm", 32).default("SHA256")
    val issuerNameHash = varchar("issuer_name_hash", 128).nullable()
    val issuerKeyHash = varchar("issuer_key_hash", 128).nullable()
    val serialNumber = varchar("serial_number", 128).nullable()
    val pem = text("pem")
    val createdAt = timestamp("created_at")
}

class ChargePointCertificateDAO(
    id: EntityID<Long>,
) : LongEntity(id) {
    companion object : LongEntityClass<ChargePointCertificateDAO>(ChargePointCertificateTable) {
        fun newInstance(
            chargePoint: ChargePointDAO,
            certificateType: String,
            certificateHash: String,
            hashAlgorithm: String,
            issuerNameHash: String?,
            issuerKeyHash: String?,
            serialNumber: String?,
            pem: String,
        ): ChargePointCertificateDAO {
            return ChargePointCertificateDAO.new {
                this.chargePoint = chargePoint
                this.certificateType = certificateType
                this.certificateHash = certificateHash
                this.hashAlgorithm = hashAlgorithm
                this.issuerNameHash = issuerNameHash
                this.issuerKeyHash = issuerKeyHash
                this.serialNumber = serialNumber
                this.pem = pem
                this.createdAt = Instant.now()
            }
        }
    }

    var chargePoint by ChargePointDAO referencedOn ChargePointCertificateTable.chargePointId
    var certificateType by ChargePointCertificateTable.certificateType
    var certificateHash by ChargePointCertificateTable.certificateHash
    var hashAlgorithm by ChargePointCertificateTable.hashAlgorithm
    var issuerNameHash by ChargePointCertificateTable.issuerNameHash
    var issuerKeyHash by ChargePointCertificateTable.issuerKeyHash
    var serialNumber by ChargePointCertificateTable.serialNumber
    var pem by ChargePointCertificateTable.pem
    var createdAt by ChargePointCertificateTable.createdAt
}
