# Remote control for OCPP Emulator

Drives a **running** `OcppEmulator` instance from an external test process — connect/
disconnect a charge point, plug/unplug a simulated car, force a connector's status/error
code — without clicking through the GUI. Built for CSMS teams who want to script charge
point behavior from their own integration test suite (pytest, Pester, a CI shell script,
...).

This directory holds the two reference clients:

```
remotecontrol/
  python/        pip-installable package + standalone CLI script
  powershell/    importable module + standalone CLI script
```

Full design rationale and the open questions still under discussion live in
[`docs/cli/cli-plan.md`](../docs/cli/cli-plan.md). Review history for this feature is in
[`docs/cli/cli-code-review.md`](../docs/cli/cli-code-review.md). This README is the
practical "how do I use it" companion to those two.

## Why a socket, not a second CLI process or an HTTP API

The emulator holds each charge point's live OCPP websocket to the CSMS *in memory*, inside
the already-running GUI process. A second, one-shot CLI process (`ocpp-emulator connect
CP001`, exit) would have no access to that live connection — it could only edit the SQLite
DB directly, which desyncs the DB from reality and sends nothing to the CSMS. So control
has to happen *inside* the running process.

Between an embedded HTTP server and a raw socket, we picked a **TCP socket speaking
newline-delimited JSON (NDJSON)**, bound to `127.0.0.1` only:

- No new runtime dependency. `java.net.ServerSocket` plus the coroutines and Jackson
  already on the classpath are enough — an HTTP API would have required pulling in
  ktor-server (today `:app` only depends on the ktor **client** bundle).
- JSON avoids inventing an ad-hoc `key=value` escaping scheme for structured/optional
  params (`vendorId`, `info`, `forceUpdate`, ...).
- Loopback-only because this is a local test-control channel, not a network service — see
  open question 1 in `cli-plan.md` if that ever needs to cross a container boundary.

Every command is a thin dispatch onto **the exact function the corresponding GUI button
already calls** (e.g. `connector.setCarState` → `ChargePointConnectorDAO.setConnectorCarState`,
the same call `VehicleStateView` makes). Nothing new was taught to the emulator; this is a
second front door onto logic the GUI already exercises.

## Changes on the emulator side

### How `OcppEmulator` starts, now

`main()` gained an `args: Array<String>` parameter, parsed by
[`platform/util/CliArgs.kt`](../app/src/jvmMain/kotlin/com/monta/ocpp/emulator/platform/util/CliArgs.kt)
before anything else happens:

| Invocation | Behavior |
|---|---|
| `OcppEmulator` | Unchanged — GUI only, no control socket. This is still the default. |
| `OcppEmulator -integration` | Also starts the control socket (`ControlServerService.start()`), right after the database connects. Stopped again from the existing JVM shutdown hook, alongside `connectionManager.disconnectAll()`. |
| `OcppEmulator -integration --db <name>` | Same, but opens `~/monta/<name>` instead of the default `~/monta/app.db` (created if missing). `--db` is rejected unless `-integration` is also passed. Lets separate integration-test scenarios keep separate charge point configurations without clobbering the developer's normal DB. |
| `OcppEmulator -?` / `-h` / `--help` | Prints usage and exits 0 — nothing else starts. |
| anything else | Prints an error to stderr and exits 1. |

Supporting change: `DatabaseService` took a `databaseName` constructor parameter
(default `"app.db"`, exposed as `DEFAULT_DATABASE_NAME`) instead of hardcoding it, and
`App.kt` now constructs it directly (`DatabaseService(databaseName = cliArgs.databaseName
?: DEFAULT_DATABASE_NAME)`) rather than pulling a Koin-managed singleton, since it's the
only consumer and the name has to vary per invocation.

### New code

```
app/src/jvmMain/kotlin/com/monta/ocpp/emulator/
  control/
    model/     ControlEnvelope.kt   — ControlRequest/ControlResponse envelope DTOs
               ControlParams.kt     — per-command request param DTOs
               ControlResults.kt    — per-command result DTOs
    exception/ ControlCommandException.kt
    service/   ControlServerService.kt      — owns the ServerSocket, accept loop, per-connection
                                               read loop, NDJSON framing
               ControlCommandDispatcher.kt  — command name -> handler; resolves identity/connector
                                               and calls existing domain services
  platform/util/CliArgs.kt          — process argument parsing (above)
```

Follows the project's existing role-folder convention (`model/` = plain types,
`service/` = orchestration), sibling to `interceptor/` — a feature that dispatches into
existing domain services rather than being part of the domain itself. `control/` imports
no Compose, same as the domain and protocol layers.

`ControlServerService`:
- `@Singleton`, auto-registered by the existing `@ComponentScan`, no manual DI wiring.
- Binds `127.0.0.1:<port>` where port defaults to the `OCPP_EMULATOR_CONTROL_PORT` env var
  or `9911`; `start(port = 0)` binds an OS-assigned ephemeral port (used by tests).
- Accept loop and each client's read loop run on plain daemon `Thread`s (not coroutines) —
  each accepted connection is handled independently so multiple test clients, or one client
  holding a connection open for a whole test run, can issue commands concurrently.
- Per line in: parses to a raw Jackson tree first so a caller-supplied `id` can still be
  echoed back in the error response even when the JSON fails to bind to `ControlRequest`
  (e.g. a well-formed object missing the required `command` field) — see finding #2 in
  `cli-code-review.md`.

`ControlCommandDispatcher`:
- One `dispatch(request)` entry point; catches `ControlCommandException` (mapped to its own
  error code), `ChargePointNotFoundException` (→ `CHARGE_POINT_NOT_FOUND`), and anything
  else (→ `INTERNAL_ERROR`, logged).
- `chargePoint.connect` polls `ChargePointDAO.connected` for up to 10s rather than joining
  a coroutine — `ConnectionManager.connect()` launches a coroutine that runs for the whole
  websocket session and never completes just because the connect succeeded.
- `chargePoint.disconnect` bounds its wait the same way (10s `withTimeoutOrNull`), since
  `ChargePointConnection.disconnect()` sends a `StatusNotificationRequest` over the live
  socket before closing it, which can block if the CSMS is slow/unreachable.

Minor supporting touch: `ConnectionManager.connect()` got a doc comment (no behavior
change) clarifying the above for future readers.

### New dependencies

**None.** No new Gradle dependency was added for the control socket itself — it's built
entirely from `java.net.ServerSocket`/`Socket` (JDK stdlib), the project's existing
coroutines, and the existing `MontaSerialization`/Jackson object mapper. `settings.gradle.kts`
picked up the `org.gradle.toolchains.foojay-resolver-convention` plugin (JDK 25 toolchain
auto-provisioning), unrelated to the control socket itself.

The two client packages in this directory are new, separate artifacts (not part of the
Gradle build):
- **Python**: stdlib only (`argparse`, `json`, `socket`) — no third-party packages.
  Requires Python ≥ 3.10.
- **PowerShell**: built-in `System.Net.Sockets.TcpClient` / `ConvertTo-Json` /
  `ConvertFrom-Json` — no external modules. Requires PowerShell ≥ 5.1.

## Wire protocol (summary)

One JSON object per line, in each direction, over a persistent TCP connection:

```jsonc
// request
{"id": "1", "command": "connector.setCarState", "params": {"identity": "CP001", "connectorId": 1, "carState": "B"}}
// success
{"id": "1", "ok": true, "result": {}}
// error
{"id": "1", "ok": false, "error": {"code": "CHARGE_POINT_NOT_FOUND", "message": "no charge point with identity CP001"}}
```

`id` is caller-supplied and optional; when present it's echoed back unchanged so a client
can pipeline several commands on one connection and match responses to requests. A
malformed line or unknown command produces an error response on that line — it does not
close the connection.

Charge points are addressed by **OCPP identity** (the string configured on the charge
point); connectors by **position**, defaulting to `1` when omitted. Command catalog:

| command | params | notes |
|---|---|---|
| `hello` | — | emulator version, for a connectivity smoke test |
| `chargePoint.connect` | `identity` | blocks up to 10s for the websocket to come up |
| `chargePoint.disconnect` | `identity` | blocks up to 10s |
| `chargePoint.setAvailability` | `identity`, `status` | `Available` \| `Unavailable` |
| `chargePoint.getState` | `identity` | read-only |
| `connector.setCarState` | `identity`, `connectorId?`, `carState` | `A`\|`B`\|`C` (Unplugged/Plugged/Ready) |
| `connector.setStatus` | `identity`, `connectorId?`, `status`, `errorCode?`, `vendorId?`, `vendorErrorCode?`, `info?` | raw override, for fault injection |
| `connector.authorize` | `identity`, `connectorId?`, `idTag` | starts/stops a transaction, like the RFID dialog |
| `connector.stopTransaction` | `identity`, `connectorId?`, `reason?`, `endReasonDescription?` | |
| `connector.getState` | `identity`, `connectorId?` | read-only |

`status`/`errorCode`/`carState`/`reason` are the exact enum names from OCPP 1.6's
`ChargePointStatus`/`ChargePointErrorCode`/`CarState`/`Reason` (e.g. `SuspendedEVSE`,
`NoError`) — no remapping.

## Python: `ocpp_emulator_remote_control`

### Install

```shell
cd remotecontrol/python
pip install -e .
```

Or use `remotecontrol.py`/`ocpp_emulator_remote_control/` directly without installing —
both only need Python ≥ 3.10 stdlib.

### As a library

```python
from ocpp_emulator_remote_control import ControlClient

with ControlClient(host="127.0.0.1", port=9911, timeout=15.0) as client:
    client.connect("CP001")
    client.plug("CP001", connector_id=1)          # CarState "C" — plugged in and ready
    client.set_connector_status("CP001", connector_id=1, error="OverVoltage")
    client.unplug("CP001", connector_id=1)         # CarState "A"
    client.disconnect("CP001")
```

`ControlClient(host, port, timeout)` — `timeout` (default `15.0`s) bounds the socket read;
sized with margin over the emulator's internal 10s `chargePoint.connect` timeout.

Methods: `hello()`, `connect(identity)`, `disconnect(identity)`,
`set_car_state(identity, car_state, connector_id=1)`, `plug(identity, connector_id=1)`,
`unplug(identity, connector_id=1)`,
`set_connector_status(identity, connector_id=1, status=None, error=None)` — `status`/`error`
are independent, pass either or both; whichever is omitted is read from the connector's
current state first and resent unchanged (matches the GUI dialog's pre-fill behavior).
`status`/`error` values are matched case-insensitively against the OCPP enum names and
normalized before being sent. Any other command from the catalog above can be issued with
the low-level `send(command, **params)`, which returns the parsed `result` dict and raises
`RuntimeError` on an error response.

### CLI

```shell
python remotecontrol.py hello [--host HOST] [--port PORT]
python remotecontrol.py connect CP001
python remotecontrol.py disconnect CP001
python remotecontrol.py plug CP001:2                          # connector defaults to 1 if omitted
python remotecontrol.py unplug CP001:2
python remotecontrol.py connector-status CP001:2 --status Faulted --error NoError
python remotecontrol.py connector-status CP001:2 --error OverVoltage   # --status/--error independent
```

Global options: `--host` (default `127.0.0.1`), `--port` (default `9911`). Charge
point/connector targets take the form `CP` or `CP:connector`.

## PowerShell: `OcppEmulatorRemoteControl`

Mirrors the Python client — same commands, same semantics.

### As a module

```powershell
Import-Module remotecontrol\powershell\OcppEmulatorRemoteControl.psd1

$client = New-OcppControlClient -ComputerName '127.0.0.1' -Port 9911 -TimeoutSec 15
try {
    Connect-OcppChargePoint -Client $client -Identity 'CP001'
    Set-OcppConnectorReady -Client $client -Identity 'CP001' -ConnectorId 1      # CarState "C"
    Set-OcppConnectorStatus -Client $client -Identity 'CP001' -ConnectorId 1 -ErrorCode 'OverVoltage'
    Set-OcppConnectorUnplugged -Client $client -Identity 'CP001' -ConnectorId 1  # CarState "A"
    Disconnect-OcppChargePoint -Client $client -Identity 'CP001'
} finally {
    Close-OcppControlClient -Client $client
}
```

Exported functions: `New-OcppControlClient [-ComputerName <string>] [-Port <int>] [-TimeoutSec <int>]`,
`Close-OcppControlClient -Client <client>`,
`Send-OcppControlCommand -Client <client> -Command <string> [-Params <hashtable>]` (low-level
escape hatch for any command in the catalog),
`Invoke-OcppHello -Client <client>`,
`Connect-OcppChargePoint -Client <client> -Identity <string>`,
`Disconnect-OcppChargePoint -Client <client> -Identity <string>`,
`Set-OcppCarState -Client <client> -Identity <string> [-ConnectorId <int>] -CarState <A|B|C>`,
`Set-OcppConnectorReady -Client <client> -Identity <string> [-ConnectorId <int>]`,
`Set-OcppConnectorUnplugged -Client <client> -Identity <string> [-ConnectorId <int>]`,
`Set-OcppConnectorStatus -Client <client> -Identity <string> [-ConnectorId <int>] [-Status <string>] [-ErrorCode <string>]`
(`-Status`/`-ErrorCode` independent, same pre-fill behavior as the Python client;
case-insensitive against the OCPP enum names).

### CLI

```powershell
.\remotecontrol.ps1 hello
.\remotecontrol.ps1 connect CP001
.\remotecontrol.ps1 disconnect CP001
.\remotecontrol.ps1 plug CP001:2
.\remotecontrol.ps1 unplug CP001:2
.\remotecontrol.ps1 connector-status CP001:2 -Status Faulted -ErrorCode NoError
.\remotecontrol.ps1 connector-status CP001:2 -ErrorCode OverVoltage
```

Params: `-Action <hello|connect|disconnect|plug|unplug|connector-status>` (positional),
`-Target <string>` (positional; identity for `connect`/`disconnect`, `CP` or `CP:connector`
for `plug`/`unplug`/`connector-status`), `-Status`, `-ErrorCode`,
`-ComputerName` (default `127.0.0.1`), `-Port` (default `9911`).

## End-to-end example

```shell
# start the emulator with the control socket, against a throwaway DB
./gradlew :app:run --args="-integration --db integration-test.db"

# from another shell, once a charge point named CP001 exists in that DB
python remotecontrol/python/remotecontrol.py connect CP001
python remotecontrol/python/remotecontrol.py plug CP001
python remotecontrol/python/remotecontrol.py connector-status CP001 --error OverVoltage
python remotecontrol/python/remotecontrol.py disconnect CP001
```

## Not yet covered

Deliberately out of scope for this MVP (see `cli-plan.md` §12): authentication (loopback
trust boundary only), a headless launch mode, and the phase-2 command catalog (firmware
status, security events, raw message injection, interceptor rule configuration). All are
cheap to add later — every command is the same shape (thin dispatch onto an existing
function).
