package com.monta.ocpp.emulator.chargepoint.model

import com.monta.ocpp.emulator.eichrecht.EichrechtKey

class ChargePointConfiguration : HashMap<String, String?>() {

    @Suppress("PropertyName", "ConstPropertyName")
    companion object {
        const val freeChargingKey = "FreeCharging"
        private const val freeChargingIdTagKey = "FreeChargingIdTag"

        private const val meterPublicKey = "MeterPublicKey"
        private const val meterPrivateKey = "MeterPrivateKey"
    }

    init {
        this["HeartbeatInterval"] = "0"
        this["GetConfigurationMaxKeys"] = "10"
        this["ConnectionTimeOut"] = "180"
        this["LocalPreAuthorize"] = "false"
        this["LocalAuthorizeOffline"] = "true"
        this["MeterValuesAlignedData"] = "Energy.Active.Import.Register,Current.Import,Voltage,Power.Active.Import,SoC"
        this["MeterValuesSampledData"] = "Energy.Active.Import.Register,Current.Import,Voltage,Power.Active.Import,SoC"
        this["SendLocalListMaxLength"] = "10"
        this["ClockAlignedDataInterval"] = "0"
        this["MeterValueSampleInterval"] = "180"
        this["SupportedFeatureProfiles"] = "Core"
        this["AuthorizationCacheEnabled"] = "false"
        this["AuthorizeRemoteTxRequests"] = "true"
        this["AllowOfflineTxForUnknownId"] = "false"
        this["StopTransactionOnInvalidId"] = "true"
        this["StopTransactionOnEVSideDisconnect"] = "true"
        this["UnlockConnectorOnEVSideDisconnect"] = "true"
        this["UnlockConnectorOnEVSideDisconnect"] = "true"
        this[freeChargingKey] = "false"
        this[freeChargingIdTagKey] = "FFFFFFFF"
        /**
         * California v2 default price for displays
         */
        this["DefaultPrice"] = ""
        this["PaymentCurrency"] = "EUR"
        this["DisplayTariffInformation"] = "none"
    }

    val meterValueSampleInterval: Long
        get() = this["MeterValueSampleInterval"]?.toLongOrNull() ?: 0L
    val meterValuesSampledData: List<String>
        get() = this["MeterValuesSampledData"]?.split(",")?.map { it.trim() } ?: listOf()

    var eichrechtKey: EichrechtKey
        set(value) {
            this[meterPublicKey] = value.publicKey()
            this[meterPrivateKey] = value.privateKey()
        }
        get() {
            val publicKey = this[meterPublicKey]
            val privateKey = this[meterPrivateKey]
            if ((publicKey != null) && (privateKey != null)) {
                return EichrechtKey.parseFromHexParts(
                    publicKey = publicKey,
                    privateKey = privateKey,
                )
            } else {
                return EichrechtKey.newInstance()
            }
        }

    var heartbeatInterval: Long
        set(value) {
            this["HeartbeatInterval"] = value.toString()
        }
        get() {
            return this["HeartbeatInterval"]?.toLongOrNull() ?: 0L
        }

    var freeCharging: Boolean
        set(value) {
            this[freeChargingKey] = value.toString()
        }
        get() {
            return this[freeChargingKey].toBoolean()
        }
    var freeChargingIdTag: String
        set(value) {
            this[freeChargingIdTagKey] = value
        }
        get() {
            return this[freeChargingIdTagKey]!!
        }
}
