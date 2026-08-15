<#
.SYNOPSIS
Client library for the OCPP emulator's control socket.

.DESCRIPTION
See docs/cli/cli-plan.md (in the ocpp-emulator repo) for the protocol design. The
emulator listens on 127.0.0.1:9911 by default (override with the
OCPP_EMULATOR_CONTROL_PORT env var on the emulator side) and speaks newline-delimited
JSON: one request object per line in, one response object per line out.

Mirrors the Python client (remotecontrol/python/ocpp_emulator_remote_control) - same
commands, same semantics. Import this module directly from a Pester test project:

    Import-Module path\to\OcppEmulatorRemoteControl.psd1
    $client = New-OcppControlClient
    Connect-OcppChargePoint -Client $client -Identity 'CP001'
    Set-OcppConnectorReady -Client $client -Identity 'CP001' -ConnectorId 2
    Close-OcppControlClient -Client $client
#>

Set-StrictMode -Version Latest

$Script:DefaultPort = 9911

# Wire values for ChargePointStatus / ChargePointErrorCode (see
# com.monta.library.ocpp.v16.core in the emulator's OCPP library). The control socket's
# JSON deserialization is case-sensitive on these exact names, so callers are matched
# case-insensitively against this list and normalized before being sent.
$Script:ChargePointStatusValues = @(
    'Available',
    'Preparing',
    'Charging',
    'SuspendedEVSE',
    'SuspendedEV',
    'Finishing',
    'Reserved',
    'Unavailable',
    'Faulted'
)

$Script:ChargePointErrorCodeValues = @(
    'ConnectorLockFailure',
    'EVCommunicationError',
    'GroundFailure',
    'HighTemperature',
    'InternalError',
    'LocalListConflict',
    'NoError',
    'OtherError',
    'OverCurrentFailure',
    'OverVoltage',
    'PowerMeterFailure',
    'PowerSwitchFailure',
    'ReaderFailure',
    'ResetFailure',
    'UnderVoltage',
    'WeakSignal'
)

function ConvertTo-OcppEnumValue {
    [CmdletBinding()]
    [OutputType([string])]
    param(
        [Parameter(Mandatory)]
        [string]$Value,

        [Parameter(Mandatory)]
        [string[]]$ValidValues,

        [Parameter(Mandatory)]
        [string]$Label
    )

    foreach ($validValue in $ValidValues) {
        if ($validValue -ieq $Value) {
            return $validValue
        }
    }

    throw "invalid $Label '$Value', expected one of: $($ValidValues -join ', ')"
}

function New-OcppControlClient {
    <#
    .SYNOPSIS
    Opens a connection to the emulator's control socket.
    #>
    [CmdletBinding()]
    [OutputType([PSCustomObject])]
    param(
        [string]$ComputerName = '127.0.0.1',
        [int]$Port = $Script:DefaultPort,

        # `chargePoint.connect` blocks server-side until the charge point's websocket is
        # up or a 10s internal timeout elapses (see ControlCommandDispatcher.awaitConnected
        # in the emulator) - give that room plus margin for the connection attempt itself.
        [int]$TimeoutSec = 15
    )

    $tcpClient = [System.Net.Sockets.TcpClient]::new()
    $tcpClient.ReceiveTimeout = $TimeoutSec * 1000
    $tcpClient.SendTimeout = $TimeoutSec * 1000
    $tcpClient.Connect($ComputerName, $Port)

    $stream = $tcpClient.GetStream()
    $reader = [System.IO.StreamReader]::new($stream, [System.Text.Encoding]::UTF8)
    $writer = [System.IO.StreamWriter]::new($stream, [System.Text.Encoding]::UTF8)
    $writer.AutoFlush = $true

    [PSCustomObject]@{
        PSTypeName = 'OcppEmulatorRemoteControl.Client'
        TcpClient  = $tcpClient
        Stream     = $stream
        Reader     = $reader
        Writer     = $writer
        NextId     = 0
    }
}

function Close-OcppControlClient {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client
    )

    $Client.Reader.Dispose()
    $Client.Writer.Dispose()
    $Client.Stream.Dispose()
    $Client.TcpClient.Close()
}

function Send-OcppControlCommand {
    <#
    .SYNOPSIS
    Sends one request and returns the parsed `result` object. Throws on an error response
    or a closed connection.
    #>
    [CmdletBinding()]
    [OutputType([PSCustomObject])]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client,

        [Parameter(Mandatory)]
        [string]$Command,

        [hashtable]$Params = @{}
    )

    $Client.NextId++
    $request = [ordered]@{
        id      = "$($Client.NextId)"
        command = $Command
        params  = $Params
    }

    $json = $request | ConvertTo-Json -Compress -Depth 6
    $Client.Writer.Write($json + "`n")

    $line = $Client.Reader.ReadLine()
    if ($null -eq $line) {
        throw 'control server closed the connection'
    }

    $response = $line | ConvertFrom-Json

    if (-not $response.ok) {
        $code = if ($response.error) { $response.error.code } else { 'UNKNOWN_ERROR' }
        $message = if ($response.error) { $response.error.message } else { '' }
        throw "${code}: ${message}"
    }

    if ($null -eq $response.result) {
        return [PSCustomObject]@{}
    }

    return $response.result
}

function Invoke-OcppHello {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client
    )

    Send-OcppControlCommand -Client $Client -Command 'hello'
}

function Connect-OcppChargePoint {
    <#
    .SYNOPSIS
    Brings the charge point with the given OCPP identity online (connects its websocket
    to the CSMS). Mirrors the GUI's connect/disconnect toggle.
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client,

        [Parameter(Mandatory)]
        [string]$Identity
    )

    Send-OcppControlCommand -Client $Client -Command 'chargePoint.connect' -Params @{
        identity = $Identity
    }
}

function Disconnect-OcppChargePoint {
    <#
    .SYNOPSIS
    Takes the charge point with the given OCPP identity offline (closes its websocket to
    the CSMS). Mirrors the GUI's connect/disconnect toggle.
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client,

        [Parameter(Mandatory)]
        [string]$Identity
    )

    Send-OcppControlCommand -Client $Client -Command 'chargePoint.disconnect' -Params @{
        identity = $Identity
    }
}

function Set-OcppCarState {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client,

        [Parameter(Mandatory)]
        [string]$Identity,

        [int]$ConnectorId = 1,

        [Parameter(Mandatory)]
        [ValidateSet('A', 'B', 'C')]
        [string]$CarState
    )

    Send-OcppControlCommand -Client $Client -Command 'connector.setCarState' -Params @{
        identity    = $Identity
        connectorId = $ConnectorId
        carState    = $CarState
    }
}

function Set-OcppConnectorReady {
    <#
    .SYNOPSIS
    Simulates the car being plugged in AND ready to charge - CarState "C", the GUI's
    "Ready" button. Not the same as the GUI's "Plugged" button (CarState "B"), which
    simulates a cable connected but not yet ready.
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client,

        [Parameter(Mandatory)]
        [string]$Identity,

        [int]$ConnectorId = 1
    )

    Set-OcppCarState -Client $Client -Identity $Identity -ConnectorId $ConnectorId -CarState 'C'
}

function Set-OcppConnectorUnplugged {
    <#
    .SYNOPSIS
    Simulates the car being unplugged - CarState "A", the GUI's "Unplugged" button.
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client,

        [Parameter(Mandatory)]
        [string]$Identity,

        [int]$ConnectorId = 1
    )

    Set-OcppCarState -Client $Client -Identity $Identity -ConnectorId $ConnectorId -CarState 'A'
}

function Set-OcppConnectorStatus {
    <#
    .SYNOPSIS
    Forces a connector's raw status and/or error code - the GUI's "Connector Status"
    dialog. -Status/-ErrorCode are independent: pass either or both. Whichever is omitted
    is read from the connector's current state first and resent unchanged, matching the
    GUI dialog (which always pre-fills both fields and sends both).
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client,

        [Parameter(Mandatory)]
        [string]$Identity,

        [int]$ConnectorId = 1,

        [string]$Status,

        [string]$ErrorCode
    )

    if ($Status) {
        $Status = ConvertTo-OcppEnumValue -Value $Status -ValidValues $Script:ChargePointStatusValues -Label 'status'
    }
    if ($ErrorCode) {
        $ErrorCode = ConvertTo-OcppEnumValue -Value $ErrorCode -ValidValues $Script:ChargePointErrorCodeValues -Label 'error'
    }

    if (-not $Status -or -not $ErrorCode) {
        $current = Send-OcppControlCommand -Client $Client -Command 'connector.getState' -Params @{
            identity    = $Identity
            connectorId = $ConnectorId
        }
        if (-not $Status) { $Status = $current.status }
        if (-not $ErrorCode) { $ErrorCode = $current.errorCode }
    }

    Send-OcppControlCommand -Client $Client -Command 'connector.setStatus' -Params @{
        identity    = $Identity
        connectorId = $ConnectorId
        status      = $Status
        errorCode   = $ErrorCode
    }
}

function Get-OcppConnectorStatus {
    <#
    .SYNOPSIS
    Reads a connector's current status/error code - the read-only counterpart to
    Set-OcppConnectorStatus.
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [PSCustomObject]$Client,

        [Parameter(Mandatory)]
        [string]$Identity,

        [int]$ConnectorId = 1
    )

    Send-OcppControlCommand -Client $Client -Command 'connector.getState' -Params @{
        identity    = $Identity
        connectorId = $ConnectorId
    }
}

Export-ModuleMember -Function @(
    'New-OcppControlClient',
    'Close-OcppControlClient',
    'Send-OcppControlCommand',
    'Invoke-OcppHello',
    'Connect-OcppChargePoint',
    'Disconnect-OcppChargePoint',
    'Set-OcppCarState',
    'Set-OcppConnectorReady',
    'Set-OcppConnectorUnplugged',
    'Set-OcppConnectorStatus',
    'Get-OcppConnectorStatus'
)
