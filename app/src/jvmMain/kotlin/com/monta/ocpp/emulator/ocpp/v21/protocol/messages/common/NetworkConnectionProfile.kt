// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class NetworkConnectionProfile(
    val ocppInterface: OCPPInterfaceEnum,
    val ocppTransport: OCPPTransportEnum,
    /** Duration in seconds before a message send by the Charging Station via this network connection times-out. The best setting depends on the underlying network and response times of the CSMS. If you are looking for a some guideline: use 30 seconds as a starting point. */
    val messageTimeout: Int,
    /** URL of the CSMS(s) that this Charging Station communicates with, without the Charging Station identity part. + The SecurityCtrlr.Identity field is appended to _ocppCsmsUrl_ to provide the full websocket URL. */
    val ocppCsmsUrl: String,
    /** This field specifies the security profile used when connecting to the CSMS with this NetworkConnectionProfile. */
    val securityProfile: Int,
    val apn: APN? = null,
    val ocppVersion: OCPPVersionEnum? = null,
    /** *(2.1)* Charging Station identity to be used as the basic authentication username. */
    val identity: String? = null,
    /** *(2.1)* BasicAuthPassword to use for security profile 1 or 2. */
    val basicAuthPassword: String? = null,
    val vpn: VPN? = null,
    val customData: CustomData? = null,
)
