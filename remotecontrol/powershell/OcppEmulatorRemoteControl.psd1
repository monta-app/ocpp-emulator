@{
    RootModule        = 'OcppEmulatorRemoteControl.psm1'
    ModuleVersion     = '0.1.0'
    GUID              = '55f196b5-964a-4416-8982-ecae23397045'
    Author            = 'Monta'
    Description       = "Client library for the OCPP emulator's remote-control socket."
    PowerShellVersion = '5.1'

    FunctionsToExport = @(
        'New-OcppControlClient',
        'Close-OcppControlClient',
        'Send-OcppControlCommand',
        'Invoke-OcppHello',
        'Connect-OcppChargePoint',
        'Disconnect-OcppChargePoint',
        'Set-OcppCarState',
        'Set-OcppConnectorReady',
        'Set-OcppConnectorUnplugged',
        'Set-OcppConnectorStatus'
    )
    CmdletsToExport   = @()
    VariablesToExport = @()
    AliasesToExport   = @()
}
