package com.monta.ocpp.emulator.eichrecht

import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.HexFormat

class EichrechtKey(
    private val keyPair: KeyPair,
) {

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }

        private const val ALGORITHM = "ECDSA"
        private const val EC_CURVE = "secp256k1"
        private const val HASH_ALGORITHM = "SHA256"
        private const val PROVIDER = "BC"

        internal val ecSpec = requireNotNull(ECNamedCurveTable.getParameterSpec(EC_CURVE))
        internal val signatureAlgorithm = "$ALGORITHM-$EC_CURVE-$HASH_ALGORITHM"
        private val curve = requireNotNull(SECNamedCurves.getByName(EC_CURVE))
        private val domain = ECDomainParameters(curve.curve, curve.g, curve.n, curve.h)

        fun newInstance(): EichrechtKey {
            val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER)
            keyPairGenerator.initialize(ecSpec, SecureRandom())

            return EichrechtKey(
                keyPair = keyPairGenerator.generateKeyPair(),
            )
        }

        /**
         * @return a [EichrechtKey] parsed from its hex parts.
         */
        fun parseFromHexParts(
            publicKey: String,
            privateKey: String,
        ): EichrechtKey {
            val keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER)
            val publicPart = keyFactory.generatePublic(X509EncodedKeySpec(fromHex(publicKey)))
            val privatePart = keyFactory.generatePrivate(PKCS8EncodedKeySpec(fromHex(privateKey)))

            return EichrechtKey(
                keyPair = KeyPair(publicPart, privatePart),
            )
        }

        private fun toHex(
            bytes: ByteArray,
        ) = HexFormat.of().formatHex(bytes)

        private fun fromHex(
            hex: String,
        ) = HexFormat.of().parseHex(hex)
    }

    internal fun privateKeyParameters() = ECPrivateKeyParameters((keyPair.private as BCECPrivateKey).d, domain)

    /**
     * @return the hex encoded public key
     */
    fun publicKey() = toHex(keyPair.public.encoded).uppercase()

    /**
     * @return the hex encoded private key
     */
    fun privateKey() = toHex(keyPair.private.encoded).uppercase()
}
