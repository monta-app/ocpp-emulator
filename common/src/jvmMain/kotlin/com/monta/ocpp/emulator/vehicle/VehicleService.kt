package com.monta.ocpp.emulator.vehicle

import com.monta.ocpp.emulator.common.util.MontaSerialization
import com.monta.ocpp.emulator.configuration.AppConfigService
import com.monta.ocpp.emulator.vehicle.model.EnodeVehicle
import com.monta.ocpp.emulator.vehicle.model.EnodeVehicleUpdate
import com.monta.ocpp.emulator.vehicle.model.MontaApiUser
import com.monta.ocpp.emulator.vehicle.view.VehicleLogger
import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.annotation.Singleton
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.Formatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Singleton
class VehicleService(
    private val appConfigService: AppConfigService,
    private val vehicleLogger: VehicleLogger
) {

    @Suppress("PropertyName", "ConstPropertyName")
    companion object {
        private val logger = LoggerFactory.getLogger(VehicleService::class.java)
        private val mapper = MontaSerialization.getDefaultMapper()

        private const val EnodeSecretKeyKey = "enode_secret_key"
        private const val VehicleIntegrationExternalIdKey = "vehicle_integration_external_id"
        private const val VehicleExternalIdKey = "vehicle_external_id"
        private const val VehicleServiceUrlKey = "vehicle_service_url"

        private const val HMAC_SHA1 = "HmacSHA1"
    }

    fun getEnodeSecretKey(): String {
        return appConfigService.getByKey(EnodeSecretKeyKey) ?: ""
    }

    fun getVehicleIntegrationExternalId(): String {
        return appConfigService.getByKey(VehicleIntegrationExternalIdKey) ?: ""
    }

    fun getVehicleExternalId(): String {
        return appConfigService.getByKey(VehicleExternalIdKey) ?: ""
    }

    fun getVehicleServiceUrl(): String {
        return appConfigService.getByKey(VehicleServiceUrlKey) ?: ""
    }

    fun store(
        enodeSecretKey: String,
        vehicleIntegrationExternalId: String,
        vehicleExternalId: String,
        vehicleServiceUrl: String
    ) {
        appConfigService.upsert(
            EnodeSecretKeyKey to enodeSecretKey,
            VehicleIntegrationExternalIdKey to vehicleIntegrationExternalId,
            VehicleExternalIdKey to vehicleExternalId,
            VehicleServiceUrlKey to vehicleServiceUrl
        )
    }

    suspend fun sendUpdate(
        integrationExternalId: String,
        vehicle: EnodeVehicle,
        host: String,
        enodeSecretKey: String
    ) {
        logger.info("sending vehicle update")
        val webhookUrl = "$host/api/v1/external-services/enode/webhooks/firehose"
        val payloadObject = enodeVehicleUpdatePayload(integrationExternalId, vehicle)
        val payload = mapper.writeValueAsString(payloadObject)
        val signature = generateSignature(enodeSecretKey, payload)
        val client = HttpClient {
            install(Logging) {
                level = LogLevel.INFO
            }
        }
        val response = client.post(webhookUrl) {
            header("X-Enode-Signature", signature)
            setBody(payload)
            contentType(ContentType.Application.Json)
        }
        if (response.status == HttpStatusCode.NoContent) {
            vehicleLogger.info(message = "Vehicle update sent successfully")
        } else {
            vehicleLogger.warn(message = "Vehicle update failed with status ${response.status}")
        }

        client.close()
    }

    private fun generateSignature(
        enodeSecretKey: String,
        payload: String
    ): String {
        val signingKey = SecretKeySpec(enodeSecretKey.toByteArray(), HMAC_SHA1)
        val hmac = Mac.getInstance(HMAC_SHA1)
        hmac.init(signingKey)
        return "sha1=" + hmac.doFinal(payload.toByteArray()).toHexString()
    }

    private fun ByteArray.toHexString(): String {
        val formatter = Formatter()
        for (b in this) {
            formatter.format("%02x", b)
        }
        return formatter.toString()
    }

    private fun enodeVehicleUpdatePayload(
        userId: String,
        vehicle: EnodeVehicle
    ): List<EnodeVehicleUpdate> {
        val now = Instant.now()
        return listOf(
            EnodeVehicleUpdate(
                event = "user:vehicle:updated",
                createdAt = now,
                user = MontaApiUser(
                    id = userId
                ),
                vehicle = vehicle
            )
        )
    }
}
