package com.monta.ocpp.emulator.eichrecht

import com.monta.ocpp.emulator.common.util.MontaSerialization
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DERSequenceGenerator
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.time.Instant
import java.util.HexFormat

class EichrechtSignatureService(
    private val chargePointIdentity: String,
    private val brand: String,
    private val model: String,
    private val serial: String,
    private val firmware: String
) {

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }

        /*
         * various OCMF constants
         */
        private const val TIME_STATUS_SYNCHRONIZED = 'S'
        private const val TRANSACTION_READING_BEGIN = 'B'
        private const val TRANSACTION_READING_END = 'E'
        private const val STATE_OF_METER_OK = 'G'
        private const val METER_OBIS_CODE = "1-b:1.8.e"
    }

    /**
     * Generate a signed OCMF string using the specified [key]
     */
    fun ocmf(
        key: EichrechtKey,
        transactionId: Long,
        idTag: String,
        startMeter: Double,
        endMeter: Double,
        startTime: Instant,
        endTime: Instant
    ): String {
        val startReading = OCMFReading(
            time = OCMFReading.formatReadingTime(startTime, TIME_STATUS_SYNCHRONIZED),
            transaction = TRANSACTION_READING_BEGIN,
            value = startMeter,
            identification = METER_OBIS_CODE,
            unit = "Wh",
            status = STATE_OF_METER_OK
        )
        val endReading = OCMFReading(
            time = OCMFReading.formatReadingTime(endTime, TIME_STATUS_SYNCHRONIZED),
            transaction = TRANSACTION_READING_END,
            value = endMeter,
            identification = METER_OBIS_CODE,
            unit = "Wh",
            status = STATE_OF_METER_OK
        )

        val ocmfPayload = OCMFPayload(
            formatVersion = "1.0",
            gatewayIdentification = brand,
            gatewayVersion = firmware,
            pagination = "T$transactionId",
            meterVendor = brand,
            meterModel = model,
            meterSerial = serial,
            meterFirmware = firmware,
            identificationStatus = true,
            identificationLevel = "VERIFIED",
            identificationFlags = listOf("RFID_PLAIN", "OCPP_RS_TLS"),
            identificationType = "ISO14443",
            identificationData = idTag,
            chargePointIdentificationType = "EVSEID",
            chargePointIdentification = chargePointIdentity,
            readings = listOf(startReading, endReading)
        )
        val payload = MontaSerialization.objectMapper.writeValueAsString(ocmfPayload)

        val privateKeyParameters = key.privateKeyParameters()
        val signature = sign(privateKeyParameters, payload)

        val ocmfSignature = OCMFSignature(
            signatureAlgorithm = EichrechtKey.signatureAlgorithm,
            signatureData = signature
        )

        return "OCMF|$payload|${MontaSerialization.objectMapper.writeValueAsString(ocmfSignature)}"
    }

    private fun sign(
        privateKeyParameters: CipherParameters,
        sdData: String
    ): String {
        val hashSHA256: ByteArray = hashSHA256(sdData.toByteArray(StandardCharsets.UTF_8))
        return HexFormat.of().formatHex(sign(privateKeyParameters, hashSHA256))
    }

    private fun sign(
        privateKeyParameters: CipherParameters,
        payloadData: ByteArray
    ): ByteArray {
        val signer = ECDSASigner()
        signer.init(true, privateKeyParameters)

        val signature: Array<BigInteger> = signer.generateSignature(payloadData)
        val baos = ByteArrayOutputStream()
        try {
            val seq = DERSequenceGenerator(baos)
            seq.addObject(ASN1Integer(signature[0]))
            seq.addObject(
                ASN1Integer(
                    toCanonicalS(
                        signature[1],
                        EichrechtKey.ecSpec
                    )
                )
            )
            seq.close()
            return baos.toByteArray()
        } catch (e: IOException) {
            return ByteArray(0)
        }
    }

    /**
     * Builds a SHA 256 hash of a byte array
     *
     * @param data to hash
     * @return data as byte array hashed with SHA256
     */
    private fun hashSHA256(data: ByteArray): ByteArray {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            return md.digest(data)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Could not load SHA-256 algorithm")
        }
    }

    private fun toCanonicalS(
        s: BigInteger,
        ecSpec: ECNamedCurveParameterSpec
    ): BigInteger {
        return if (s <= ecSpec.n.shiftRight(1)) {
            s
        } else {
            ecSpec.n.subtract(s)
        }
    }
}
