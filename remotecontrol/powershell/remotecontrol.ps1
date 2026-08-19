<#
.SYNOPSIS
CLI for the OCPP emulator's control socket.

.DESCRIPTION
See remotecontrol/remotecontrol-plan.md (in the ocpp-emulator repo) for the protocol design. The client
library lives in OcppEmulatorRemoteControl.psm1 alongside this script (also usable
directly, e.g. from a Pester test project: Import-Module .\OcppEmulatorRemoteControl.psd1).

.EXAMPLE
.\remotecontrol.ps1 hello

.EXAMPLE
.\remotecontrol.ps1 connect CP001

.EXAMPLE
.\remotecontrol.ps1 disconnect CP001

.EXAMPLE
.\remotecontrol.ps1 plug CP001:2

"plug" is the GUI's "Ready" state (car plugged in and ready to charge) - not "Plugged"
(cable connected but not yet ready). ConnectorId defaults to 1 if omitted (CP001).

.EXAMPLE
.\remotecontrol.ps1 unplug CP001:2

.EXAMPLE
.\remotecontrol.ps1 set-connector-status CP001:2 -Status Faulted -ErrorCode NoError

.EXAMPLE
.\remotecontrol.ps1 set-connector-status CP001:2 -ErrorCode OverVoltage

-Status/-ErrorCode are independent for set-connector-status - pass either or both;
whichever one is omitted keeps its current value. Values are matched case-insensitively.

.EXAMPLE
.\remotecontrol.ps1 get-connector-status CP001:2

Reads a connector's current status/error code (read-only).

.EXAMPLE
.\remotecontrol.ps1 authorize CP001:2 -IdTag DEADBEEF

Presents an idTag and starts a transaction (GUI "Authorize" RFID dialog). OCPP 1.6 has
no VIN field - only idTag - so a vehicle can't be identified to the CSMS this way; encode
any vehicle correlation into the idTag by convention if needed.

.EXAMPLE
.\remotecontrol.ps1 stop-transaction CP001:2
.\remotecontrol.ps1 stop-transaction CP001:2 -Reason EVDisconnected

Stops the connector's active transaction (GUI "Stop transaction" button). -Reason
defaults to Local when omitted.
#>

[CmdletBinding()]
param(
    [Parameter(Mandatory, Position = 0)]
    [ValidateSet('hello', 'connect', 'disconnect', 'plug', 'unplug', 'set-connector-status', 'get-connector-status', 'authorize', 'stop-transaction')]
    [string]$Action,

    # identity for connect/disconnect; "CP" or "CP:connector" for plug/unplug/set-connector-status/get-connector-status/authorize/stop-transaction
    [Parameter(Position = 1)]
    [string]$Target,

    [string]$Status,
    [string]$ErrorCode,
    [string]$IdTag,
    [string]$Reason,
    [string]$EndReasonDescription,

    [string]$ComputerName = '127.0.0.1',
    [int]$Port = 9911
)

Import-Module (Join-Path $PSScriptRoot 'OcppEmulatorRemoteControl.psd1') -Force

function Split-ChargePointConnector {
    [OutputType([PSCustomObject])]
    param(
        [Parameter(Mandatory)]
        [string]$Value
    )

    if ($Value -match '^(?<identity>[^:]+)(:(?<connector>\d+))?$') {
        $connectorId = if ($Matches.connector) { [int]$Matches.connector } else { 1 }
        return [PSCustomObject]@{
            Identity    = $Matches.identity
            ConnectorId = $connectorId
        }
    }

    throw "invalid target '$Value': expected CP or CP:connector, e.g. CP001 or CP001:2"
}

if ($Action -in @('connect', 'disconnect', 'plug', 'unplug', 'set-connector-status', 'get-connector-status', 'authorize', 'stop-transaction') -and -not $Target) {
    throw "$Action requires a target (identity, or CP[:connector])"
}
if ($Action -eq 'set-connector-status' -and -not $Status -and -not $ErrorCode) {
    throw 'set-connector-status requires at least one of -Status or -ErrorCode'
}
if ($Action -eq 'authorize' -and -not $IdTag) {
    throw 'authorize requires -IdTag'
}

# Parse CP[:connector] targets up front, before opening any connection, so a malformed
# target fails fast instead of after an unnecessary socket connect.
$chargePointConnector = $null
if ($Action -in @('plug', 'unplug', 'set-connector-status', 'get-connector-status', 'authorize', 'stop-transaction')) {
    $chargePointConnector = Split-ChargePointConnector -Value $Target
}

$client = New-OcppControlClient -ComputerName $ComputerName -Port $Port
try {
    switch ($Action) {
        'hello' {
            $result = Invoke-OcppHello -Client $client
            Write-Output "emulator version: $($result.emulatorVersion)"
            Write-Output "protocol version: $($result.protocolVersion)"
        }
        'connect' {
            $result = Connect-OcppChargePoint -Client $client -Identity $Target
            Write-Output "${Target}: connected=$($result.connected)"
        }
        'disconnect' {
            $result = Disconnect-OcppChargePoint -Client $client -Identity $Target
            Write-Output "${Target}: connected=$($result.connected)"
        }
        'plug' {
            $cp = $chargePointConnector
            $result = Set-OcppConnectorReady -Client $client -Identity $cp.Identity -ConnectorId $cp.ConnectorId
            Write-Output "$($cp.Identity):$($cp.ConnectorId): carState=$($result.carState)"
        }
        'unplug' {
            $cp = $chargePointConnector
            $result = Set-OcppConnectorUnplugged -Client $client -Identity $cp.Identity -ConnectorId $cp.ConnectorId
            Write-Output "$($cp.Identity):$($cp.ConnectorId): carState=$($result.carState)"
        }
        'set-connector-status' {
            $cp = $chargePointConnector
            $result = Set-OcppConnectorStatus -Client $client -Identity $cp.Identity -ConnectorId $cp.ConnectorId -Status $Status -ErrorCode $ErrorCode
            Write-Output "$($cp.Identity):$($cp.ConnectorId): status=$($result.status) errorCode=$($result.errorCode)"
        }
        'get-connector-status' {
            $cp = $chargePointConnector
            $result = Get-OcppConnectorStatus -Client $client -Identity $cp.Identity -ConnectorId $cp.ConnectorId
            Write-Output "$($cp.Identity):$($cp.ConnectorId): status=$($result.status) errorCode=$($result.errorCode)"
        }
        'authorize' {
            $cp = $chargePointConnector
            $result = Invoke-OcppAuthorize -Client $client -Identity $cp.Identity -ConnectorId $cp.ConnectorId -IdTag $IdTag
            Write-Output "$($cp.Identity):$($cp.ConnectorId): status=$($result.status)"
        }
        'stop-transaction' {
            $cp = $chargePointConnector
            $result = Stop-OcppTransaction -Client $client -Identity $cp.Identity -ConnectorId $cp.ConnectorId -Reason $Reason -EndReasonDescription $EndReasonDescription
            Write-Output "$($cp.Identity):$($cp.ConnectorId): status=$($result.status) activeTransactionId=$($result.activeTransactionId)"
        }
    }
} finally {
    Close-OcppControlClient -Client $client
}
