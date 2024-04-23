package com.monta.ocpp.emulator.chargepoint.view.components.security

enum class SecurityEvent(
    val description: String
) {
    FirmwareUpdated(
        description = "The Charge Point firmware is updated"
    ),
    FailedToAuthenticateAtCentralSystem(
        description = "The authentication credentials provided by the Charge Point were rejected by the Central System"
    ),
    CentralSystemFailedToAuthenticate(
        description = "The authentication credentials provided by the Central System were rejected by the Charge Point"
    ),
    SettingSystemTime(
        description = "The system time on the Charge Point was changed"
    ),
    StartupOfTheDevice(
        description = "The Charge Point has booted"
    ),
    ResetOrReboot(
        description = "The Charge Point was rebooted or reset"
    ),
    SecurityLogWasCleared(
        description = "The security log was cleared"
    ),
    ReconfigurationOfSecurityParameters(
        description = "Security parameters, such as keys or the security profile used, were changed"
    ),
    MemoryExhaustion(
        description = "The Flash or RAM memory of the Charge Point is getting full"
    ),
    InvalidMessages(
        description = "The Charge Point has received messages that are not valid OCPP messages, if signed messages, signage invalid/incorrect"
    ),
    AttemptedReplayAttacks(
        description = "The Charge Point has received a replayed message (other than the Central System trying to resend a message because it there was for example a network problem)"
    ),
    TamperDetectionActivated(
        description = "The physical tamper detection sensor was triggered"
    ),
    InvalidFirmwareSignature(
        description = "The firmware signature is not valid"
    ),
    InvalidFirmwareSigningCertificate(
        description = "The certificate used to verify the firmware signature is not valid"
    ),
    InvalidCentralSystemCertificate(
        description = "The certificate that the Central System uses was not valid or could not be verified"
    ),
    InvalidChargePointCertificate(
        description = "The certificate sent to the Charge Point using the SignCertificate.conf message is not a valid certificate"
    ),
    InvalidTLSVersion(
        description = "The TLS version used by the Central System is lower than 1.2 and is not allowed by the security specification"
    ),
    InvalidTLSCipherSuite(
        description = "The Central System did only allow connections using TLS cipher suites that are not allowed by the security specification"
    )
}
