package com.monta.ocpp.emulator.platform.eichrecht.service

import com.monta.ocpp.emulator.platform.eichrecht.model.EichrechtKey
import com.monta.ocpp.emulator.platform.eichrecht.model.OCMFReading
import com.monta.ocpp.emulator.platform.util.MontaSerialization
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.HexFormat

class EichrechtSignatureServiceTest : DescribeSpec({

    val startTime: Instant = Instant.parse("2026-01-01T12:00:00Z")
    val endTime: Instant = Instant.parse("2026-01-01T13:30:00Z")

    val service = EichrechtSignatureService(
        chargePointIdentity = "MEM_001",
        brand = "Monta",
        model = "E-Emulator",
        serial = "Emulator",
        firmware = "1.0.0",
    )

    val key = EichrechtKey.newInstance()

    fun ocmf(
        signingKey: EichrechtKey = key,
        transactionId: Long = 42,
        idTag: String = "ABCD1234",
        startMeter: Double = 1000.0,
        endMeter: Double = 7900.0,
    ): String {
        return service.ocmf(
            key = signingKey,
            transactionId = transactionId,
            idTag = idTag,
            startMeter = startMeter,
            endMeter = endMeter,
            startTime = startTime,
            endTime = endTime,
        )
    }

    fun payloadOf(
        ocmfString: String,
    ): String {
        return ocmfString.split("|")[1]
    }

    fun signatureDataOf(
        ocmfString: String,
    ): String {
        val signatureJson = MontaSerialization.objectMapper.readTree(ocmfString.split("|")[2])
        return signatureJson.get("SD").asText()
    }

    describe("envelope") {

        it("is the three pipe-separated OCMF sections") {
            val sections = ocmf().split("|")

            sections shouldHaveSize 3
            sections[0] shouldBe "OCMF"
        }

        it("names the signing algorithm the public key was generated for") {
            val signatureJson = MontaSerialization.objectMapper.readTree(ocmf().split("|")[2])

            signatureJson.get("SA").asText() shouldBe "ECDSA-secp256k1-SHA256"
        }
    }

    describe("payload") {

        it("carries the charge point and meter identity") {
            val payload = MontaSerialization.objectMapper.readTree(payloadOf(ocmf()))

            payload.get("FV").asText() shouldBe "1.0"
            payload.get("GI").asText() shouldBe "Monta"
            payload.get("MM").asText() shouldBe "E-Emulator"
            payload.get("MS").asText() shouldBe "Emulator"
            payload.get("CI").asText() shouldBe "MEM_001"
        }

        it("paginates by transaction so two transactions never share a record") {
            val payload = MontaSerialization.objectMapper.readTree(payloadOf(ocmf(transactionId = 7)))

            payload.get("PG").asText() shouldBe "T7"
        }

        it("records the authenticated id tag as verified RFID") {
            val payload = MontaSerialization.objectMapper.readTree(payloadOf(ocmf(idTag = "DEADBEEF")))

            payload.get("ID").asText() shouldBe "DEADBEEF"
            payload.get("IS").asBoolean() shouldBe true
            payload.get("IL").asText() shouldBe "VERIFIED"
        }

        it("brackets the charge with a begin and an end reading") {
            val payload = MontaSerialization.objectMapper.readTree(
                payloadOf(ocmf(startMeter = 1000.0, endMeter = 7900.0)),
            )
            val readings = payload.get("RD")

            readings.size() shouldBe 2
            readings.get(0).get("TX").asText() shouldBe "B"
            readings.get(0).get("RV").asDouble() shouldBe 1000.0
            readings.get(1).get("TX").asText() shouldBe "E"
            readings.get(1).get("RV").asDouble() shouldBe 7900.0
            readings.get(0).get("RI").asText() shouldBe "1-b:1.8.e"
            readings.get(0).get("RU").asText() shouldBe "Wh"
            readings.get(0).get("ST").asText() shouldBe "G"
        }

        it("is byte-for-byte reproducible, so the signature covers a stable record") {
            payloadOf(ocmf()) shouldBe payloadOf(ocmf())
        }
    }

    describe("signature") {

        it("verifies against the public key the charge point publishes") {
            val ocmfString = ocmf()

            val curve = SECNamedCurves.getByName("secp256k1")
            val domain = ECDomainParameters(curve.curve, curve.g, curve.n, curve.h)
            val publicKey = KeyFactory.getInstance("ECDSA", "BC").generatePublic(
                X509EncodedKeySpec(HexFormat.of().parseHex(key.publicKey())),
            ) as BCECPublicKey

            val derSignature = ASN1Sequence.getInstance(HexFormat.of().parseHex(signatureDataOf(ocmfString)))
            val r = (derSignature.getObjectAt(0) as ASN1Integer).value
            val s = (derSignature.getObjectAt(1) as ASN1Integer).value

            val digest = MessageDigest.getInstance("SHA-256")
                .digest(payloadOf(ocmfString).toByteArray(StandardCharsets.UTF_8))

            val verifier = ECDSASigner()
            verifier.init(false, ECPublicKeyParameters(publicKey.q, domain))

            verifier.verifySignature(digest, r, s) shouldBe true
        }

        it("does not verify against an unrelated key") {
            val ocmfString = ocmf(signingKey = EichrechtKey.newInstance())

            val curve = SECNamedCurves.getByName("secp256k1")
            val domain = ECDomainParameters(curve.curve, curve.g, curve.n, curve.h)
            val publicKey = KeyFactory.getInstance("ECDSA", "BC").generatePublic(
                X509EncodedKeySpec(HexFormat.of().parseHex(key.publicKey())),
            ) as BCECPublicKey

            val derSignature = ASN1Sequence.getInstance(HexFormat.of().parseHex(signatureDataOf(ocmfString)))
            val r = (derSignature.getObjectAt(0) as ASN1Integer).value
            val s = (derSignature.getObjectAt(1) as ASN1Integer).value

            val digest = MessageDigest.getInstance("SHA-256")
                .digest(payloadOf(ocmfString).toByteArray(StandardCharsets.UTF_8))

            val verifier = ECDSASigner()
            verifier.init(false, ECPublicKeyParameters(publicKey.q, domain))

            verifier.verifySignature(digest, r, s) shouldBe false
        }

        it("normalises to the low half of the curve order, as OCMF verifiers expect") {
            val curve = SECNamedCurves.getByName("secp256k1")
            val halfOrder = curve.n.shiftRight(1)

            repeat(20) { attempt ->
                val derSignature = ASN1Sequence.getInstance(HexFormat.of().parseHex(signatureDataOf(ocmf())))
                val s = (derSignature.getObjectAt(1) as ASN1Integer).value

                (s <= halfOrder) shouldBe true
            }
        }
    }

    describe("reading timestamps") {

        it("are ISO 8601 with millisecond resolution and a synchronisation flag") {
            val payload = MontaSerialization.objectMapper.readTree(payloadOf(ocmf()))

            payload.get("RD").get(0).get("TM").asText() shouldMatch
                Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2},\d{3}[+-]\d{4} S""")
        }

        it("round-trip to the instant they were formatted from, whatever the local zone is") {
            val formatted = OCMFReading.formatReadingTime(startTime, 'S')
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSSZ")

            val parsed = ZonedDateTime.parse(formatted.substringBefore(" "), formatter).toInstant()

            parsed shouldBe startTime.truncatedTo(ChronoUnit.MILLIS)
        }
    }
})
