package com.monta.ocpp.emulator.interceptor

import androidx.compose.runtime.mutableStateOf
import com.monta.library.ocpp.common.serialization.Message
import com.monta.library.ocpp.common.serialization.MessageSerializer
import com.monta.library.ocpp.common.serialization.ParsingResult
import com.monta.library.ocpp.common.serialization.SerializationMode
import com.monta.library.ocpp.v16.core.AuthorizeFeature
import com.monta.library.ocpp.v16.core.BootNotificationFeature
import com.monta.library.ocpp.v16.core.ChangeAvailabilityFeature
import com.monta.library.ocpp.v16.core.ChangeConfigurationFeature
import com.monta.library.ocpp.v16.core.ClearCacheFeature
import com.monta.library.ocpp.v16.core.DataTransferFeature
import com.monta.library.ocpp.v16.core.GetConfigurationFeature
import com.monta.library.ocpp.v16.core.HeartbeatFeature
import com.monta.library.ocpp.v16.core.MeterValuesFeature
import com.monta.library.ocpp.v16.core.RemoteStartTransactionFeature
import com.monta.library.ocpp.v16.core.RemoteStopTransactionFeature
import com.monta.library.ocpp.v16.core.ResetFeature
import com.monta.library.ocpp.v16.core.StartTransactionFeature
import com.monta.library.ocpp.v16.core.StatusNotificationFeature
import com.monta.library.ocpp.v16.core.StopTransactionFeature
import com.monta.library.ocpp.v16.core.UnlockConnectorFeature
import com.monta.library.ocpp.v16.error.OcppErrorResponderV16
import com.monta.library.ocpp.v16.firmware.DiagnosticsStatusNotificationFeature
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationFeature
import com.monta.library.ocpp.v16.firmware.GetDiagnosticsFeature
import com.monta.library.ocpp.v16.firmware.UpdateFirmwareFeature
import com.monta.library.ocpp.v16.localauth.GetLocalListVersionFeature
import com.monta.library.ocpp.v16.remotetrigger.TriggerMessageFeature
import com.monta.library.ocpp.v16.smartcharge.ClearChargingProfileFeature
import com.monta.library.ocpp.v16.smartcharge.GetCompositeScheduleFeature
import com.monta.library.ocpp.v16.smartcharge.SetChargingProfileFeature
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.logger.GlobalLogger
import com.monta.ocpp.emulator.logger.Loggable
import org.koin.core.annotation.Singleton

@Singleton
class MessageInterceptor(
    val chargePointService: ChargePointService
) {
    companion object {
        val serializer = MessageSerializer(SerializationMode.OCPP_1_6, OcppErrorResponderV16)

        val centralSystemFeatures = setOf(
            // CancelReservationFeature, not implemented?
            ChangeAvailabilityFeature,
            ChangeConfigurationFeature,
            ClearCacheFeature,
            ClearChargingProfileFeature,
            // DataTransferFeature, this should be included, but it's a headache to synchronize. perhaps a third category (both) would be better
            GetCompositeScheduleFeature,
            GetConfigurationFeature,
            GetDiagnosticsFeature,
            GetLocalListVersionFeature,
            RemoteStartTransactionFeature,
            RemoteStopTransactionFeature,
            // ReserveNowFeature, not implemented?
            ResetFeature,
            // SendLocalListFeature, not implemented?
            SetChargingProfileFeature,
            TriggerMessageFeature,
            UnlockConnectorFeature,
            UpdateFirmwareFeature
        ).associateBy { it.name }

        val chargePointFeatures = setOf(
            AuthorizeFeature,
            BootNotificationFeature,
            DataTransferFeature,
            DiagnosticsStatusNotificationFeature,
            FirmwareStatusNotificationFeature,
            HeartbeatFeature,
            MeterValuesFeature,
            StartTransactionFeature,
            StatusNotificationFeature,
            StopTransactionFeature
        ).associateBy { it.name }
    }

    // message ids to intercept
    private val messageIdsToIntercept = mutableMapOf<String, Interception>()

    // settings/config
    val messageTypeConfig = mutableMapOf<Long, Map<String, InterceptionConfig>>()

    private fun defaults() = listOf(
        AuthorizeFeature.name,
        BootNotificationFeature.name,
        // CancelReservationFeature.name,
        ChangeAvailabilityFeature.name,
        ChangeConfigurationFeature.name,
        ClearCacheFeature.name,
        ClearChargingProfileFeature.name,
        DataTransferFeature.name,
        DiagnosticsStatusNotificationFeature.name,
        FirmwareStatusNotificationFeature.name,
        GetCompositeScheduleFeature.name,
        GetConfigurationFeature.name,
        GetDiagnosticsFeature.name,
        GetLocalListVersionFeature.name,
        HeartbeatFeature.name,
        MeterValuesFeature.name,
        RemoteStartTransactionFeature.name,
        RemoteStopTransactionFeature.name,
        // ReserveNowFeature.name,
        ResetFeature.name,
        // SendLocalListFeature.name,
        SetChargingProfileFeature.name,
        StartTransactionFeature.name,
        StatusNotificationFeature.name,
        StopTransactionFeature.name,
        TriggerMessageFeature.name,
        UnlockConnectorFeature.name,
        UpdateFirmwareFeature.name
    ).associateWith {
        InterceptionConfig(
            mutableStateOf(Interception.NoOp),
            mutableStateOf(Interception.NoOp)
        )
    }.toMap()

    fun addDefaults(id: Long) {
        messageTypeConfig[id] = defaults()
    }

    // intercept send
    suspend fun intercept(
        chargePointIdentity: String,
        messageJson: String
    ): String? {
        val chargePointId = chargePointService.getByIdentity(chargePointIdentity).idValue
        return when (val result = serializer.parse(messageJson)) {
            is ParsingResult.Failure -> {
                GlobalLogger.warn(
                    object : Loggable {
                        override fun chargePointId(): Long {
                            return chargePointId
                        }

                        override fun connectorPosition(): Int {
                            return 0
                        }
                    },
                    "Unable to parse message: $messageJson"
                )
                messageJson
            }

            is ParsingResult.Success -> {
                when (val messageObject = result.value) {
                    is Message.Request -> {
                        val config = messageTypeConfig[chargePointId]?.get(messageObject.action)
                        if (config != null) {
                            val newMessage = config.onRequest.value.intercept(messageObject)
                            messageIdsToIntercept[messageObject.uniqueId] = config.onResponse.value
                            newMessage
                        } else {
                            messageJson
                        }
                    }

                    is Message.Response -> {
                        val interception = messageIdsToIntercept.remove(messageObject.uniqueId)
                        if (interception != null) {
                            interception.intercept(messageObject)
                        } else {
                            messageJson
                        }
                    }

                    is Message.Error -> {
                        messageJson
                    }
                }
            }
        }
    }
}
