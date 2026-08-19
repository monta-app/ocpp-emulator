# Remote control for integration testing — implementation plan

Status: **MVP implemented** (`app/src/jvmMain/kotlin/com/monta/ocpp/emulator/control/`,
wired into `App.kt`; reference client at `remotecontrol/python/remotecontrol.py`, backed by
the reusable `ocpp_emulator_remote_control` package in the same directory). The control socket
is opt-in: `OcppEmulator` with no arguments behaves exactly as before (no socket started).
Pass `-integration` to start it, and optionally `--db <name>` (only valid together with
`-integration`) to point the run at a different SQLite database file under `~/monta/`
instead of the default `app.db`, so separate integration test scenarios can keep separate
charge point configurations. `OcppEmulator -?`/`-h`/`--help` prints usage and exits. See
`platform/util/CliArgs.kt`. This document
captures the design for letting an external process (e.g. a CSMS's own integration-test
suite) drive a *running* emulator instance — connect/disconnect charge points,
plug/unplug cars, force connector status, start/stop transactions — without touching the
GUI. The command catalog in §7 and the open questions in §12 still apply; nothing there
has been resolved by the MVP beyond what's listed as in-scope.

**Post-MVP addition:** `connector.startTransaction` was added alongside `connector.authorize`
to cover the second of OCPP 1.6's two ways to begin a transaction (§4.1/§4.8 of the spec):
`connector.authorize` is the RFID-tap pattern (`ChargePointManager.authorize()` —
Authorize.req/.conf, then, if accepted, a separate StartTransaction.req — authorize
*before* plugging in); `connector.startTransaction` is the plug-and-charge/autocharge
pattern (`ChargePointConnectorDAO.start()` directly — a single StartTransaction.req/.conf
round trip both authorizes and starts, no separate Authorize.req). The latter reuses the
same code path `startFreeCharging()` already calls automatically when the `FreeCharging`
configuration key is enabled, just callable on demand with any idTag. Both are fully
decided by the CSMS's response; this emulator's Local Authorization List handler
(`LocalAuthHandler.kt`) only stores what the CSMS pushes down for reporting back via
`GetLocalListVersion` — it never gates either outgoing request, online or offline. Neither
command has a VIN parameter, matching §4 below: OCPP 1.6 carries only `idTag`.

## 1. Goal

The emulator today is GUI-only: every state transition (connect a charge point, plug in
a car, force a connector status) happens by clicking something in Compose. A team
building a CSMS backend wants to drive the same transitions from their own integration
test suite (e.g. JUnit/pytest/Playwright-style test code, or a CI shell script), against
an emulator instance that is already running and already holds a live OCPP websocket to
their CSMS.

## 2. Decided: control via a socket into the running process

Two other shapes were considered and rejected:

- **One-shot CLI process per action** (`ocpp-emulator connect --identity CP001`, exits).
  Rejected: the OCPP websocket to the CSMS is held in-memory by the running GUI process
  (`ChargePointConnection` inside `ConnectionManager`). A second process has no access to
  that live connection — it could only edit the SQLite DB directly, which would desync
  the DB from reality and would **not** actually send anything to the CSMS. Only code
  running *inside* the already-running process can make real protocol traffic happen.
- **Embedded HTTP API** (ktor-server + REST). More ergonomic for callers (curl, any HTTP
  client, browser devtools) but pulls in a new dependency the project doesn't have today
  (`app/build.gradle.kts` only has the ktor **client** bundle — see §9).

**Decision: a small TCP socket server embedded in the running emulator process,
listening on `127.0.0.1`, speaking newline-delimited JSON.** No new runtime dependency —
`java.net.ServerSocket` + the coroutines/Jackson already on the classpath are enough.
Test code (in any language) opens a socket, writes one JSON object per line, reads one
JSON object per line back.

## 3. What this unlocks

The ask was: can we "press the GUI buttons" programmatically? Concretely, yes — every
control below **is the exact function the corresponding GUI button already calls**, just
invoked from a socket command handler instead of a Compose `onClick`. Nothing new is
being taught to the emulator; this is a second front door onto logic that already exists
and is already exercised by the GUI.

| GUI control | Function it calls today | New CLI command (proposed) |
|---|---|---|
| Connect/disconnect toggle (`ChargePointConnectionButton`) | `ConnectionManager.connect(chargePointId: Long)` / `.disconnect(chargePointId: Long): Job?` | `chargePoint.connect`, `chargePoint.disconnect` |
| Vehicle state toggle — Unplugged/Plugged/Ready (`VehicleStateView`) | `ChargePointConnectorDAO.setConnectorCarState(carState: CarState)` | `connector.setCarState` |
| "Connector Status" dialog (`ConnectorStateView`) — raw status/error override | `ChargePointConnectorDAO.setStatus(status, errorCode, vendorId, vendorErrorCode, info, forceUpdate)` | `connector.setStatus` |
| Availability toggle — Available/Unavailable (`chargePointComponent`) | `ChargePointDAO.setStatus(status, errorCode, forceUpdate)` | `chargePoint.setAvailability` |
| "Stop transaction" button (`ConnectorCard`) | `ChargePointConnectorDAO.stopActiveTransactions(reason, endReasonDescription)` | `connector.stopTransaction` |
| RFID "Authorize" dialog (`authorizeComponent`) — starts/stops a transaction | `ChargePointManager.authorize(connector, idTag): AuthorizationStatus` | `connector.authorize` |
| *(read-only, for test assertions)* | `ChargePointService.getByIdentity`, `ChargePointConnectorService.get` | `chargePoint.getState`, `connector.getState` |

Stretch (phase 2, not needed for the stated goal but the same mechanism covers them):

| GUI control | Function | Command |
|---|---|---|
| Firmware update simulation | `ChargePointManager.startFirmwareUpdate` / `firmwareStatusNotification` | `chargePoint.setFirmwareStatus` |
| Diagnostics status | `ChargePointManager.diagnosticsStatusNotification` | `chargePoint.setDiagnosticsStatus` |
| Security event | `ChargePointManager.securityEvent` | `chargePoint.sendSecurityEvent` |
| "Send Message" window — arbitrary raw OCPP message | `OcppClientV16.sendMessage(...)` | `chargePoint.sendRawMessage` |
| Interceptor delay/drop rules (`InterceptorConfigComponent`) | `MessageInterceptor` / `InterceptionConfig` (holds Compose `MutableState`) | `interceptor.configure` — needs care, see §11 |

### Why `setCarState` and not just `setStatus` for "plug the car in"

`ChargePointConnectorDAO.calculateState()` derives the *correct* OCPP status from
`carState` (A=Unplugged→`Available`, B=Plugged→`Preparing`/`SuspendedEV`,
C=Ready→`Preparing`/`Charging`) plus whether a transaction is active. `setConnectorCarState`
calls this and pushes the derived status automatically — it's the "physically honest"
lever and matches what a real cable does. `setStatus` is a raw override (what the GUI's
"Connector Status" dialog uses) — useful for fault injection (e.g. force `Faulted`
regardless of car state) but can desync `carState` from `status`. **Recommendation: expose
both** — `connector.setCarState` for realistic plug/unplug simulation, `connector.setStatus`
for direct fault/edge-case injection — exactly mirroring the two separate GUI controls.

## 4. Non-goals for this phase

- No literal UI automation (no pixel/accessibility-tree clicking) — out of scope and
  unnecessary, since every button is a thin wrapper over a plain function call.
- No headless launch mode (`--headless` flag to skip the Compose window). Explicitly
  deferred by the user — see §12 open questions. This plan assumes the GUI keeps running;
  the control socket is additive.
- No change to `App.kt`'s existing behavior beyond one line to start the control server.

## 5. Package layout

New top-level feature package, sibling to `interceptor/` (same reasoning: it's a feature
that dispatches into existing domain services, not itself part of the domain):

```
control/
  model/     ControlCommand.kt      — sealed request DTOs (Jackson-deserialized)
             ControlResponse.kt     — response/error envelope DTOs
  service/   ControlServerService.kt    — owns the ServerSocket, accept loop, per-connection read loop
             ControlCommandDispatcher.kt — maps command name -> handler, calls into
                                            ConnectionManager / ChargePointService /
                                            ChargePointManager / the extension functions
```

This follows the project's role-folder rule (`model/` = plain types, `service/` =
orchestration) and keeps Compose out of it entirely — `control/` never imports
`androidx.compose.*`, same as the domain/protocol layers.

`ControlCommandDispatcher` is a thin translation layer only: JSON in, resolve
identity/connector, call the *same* existing suspend function the GUI calls, JSON out. It
does not reimplement any state logic.

## 6. Addressing scheme

- Charge point: addressed by **OCPP identity string** (what test authors actually know —
  it's what they configured as the charge point's identity), resolved via
  `ChargePointService.getByIdentity(identity: String): ChargePointDAO` (throws
  `ChargePointNotFoundException` if unknown — caught and turned into an error response).
- Connector: addressed by **position `Int`** within a charge point, via
  `chargePoint.getConnector(connectorId)` (self-healing — creates the row if missing,
  same as the GUI/UI code paths do) or `ChargePointConnectorService.get(chargePointId, connectorId)`.
  Default to connector `1` when omitted, matching the convention already used in
  `ChargePointManager.sendMeterValues` and `SendMessageWindow.defaultPayload`.

## 7. Protocol

Newline-delimited JSON (NDJSON) over a plain TCP socket. One JSON object per line in
each direction; a connection can carry many commands (no need to reconnect per call).
Chosen over line-based plaintext because commands have structured, sometimes-optional
parameters (`vendorId`, `info`, `forceUpdate`, ...) that don't fit a flat `key=value` line
without inventing an ad-hoc escaping scheme — JSON avoids that for free and Jackson is
already a project dependency (`MontaSerialization`/`libs.bundles.jackson`).

**Request:**
```json
{"id": "1", "command": "connector.setCarState", "params": {"identity": "CP001", "connectorId": 1, "carState": "B"}}
```

**Response (success):**
```json
{"id": "1", "ok": true, "result": {}}
```

**Response (error):**
```json
{"id": "1", "ok": false, "error": {"code": "CHARGE_POINT_NOT_FOUND", "message": "no charge point with identity CP001"}}
```

`id` is caller-supplied and echoed back unchanged, so a test client can pipeline several
commands on one connection and match responses to requests. It's optional — omit it for
simple fire-and-forget scripting (`nc localhost 9911` style).

Malformed input (bad JSON, unknown command) yields an error response on that line; it
does **not** close the connection, so one bad command in a test script doesn't kill the
rest of the session.

### Command catalog (MVP)

| command | params | calls |
|---|---|---|
| `chargePoint.connect` | `identity` | `ConnectionManager.connect(chargePointId)` |
| `chargePoint.disconnect` | `identity` | `ConnectionManager.disconnect(chargePointId)` |
| `chargePoint.setAvailability` | `identity`, `status` (`Available`\|`Unavailable`) | `ChargePointDAO.setStatus(...)` |
| `chargePoint.getState` | `identity` | `ChargePointService.getByIdentity` → serialize `connected`, `status`, `errorCode`, connector summaries |
| `connector.setCarState` | `identity`, `connectorId?`, `carState` (`A`\|`B`\|`C`) | `ChargePointConnectorDAO.setConnectorCarState(...)` |
| `connector.setStatus` | `identity`, `connectorId?`, `status`, `errorCode?`, `vendorId?`, `vendorErrorCode?`, `info?` | `ChargePointConnectorDAO.setStatus(..., forceUpdate = true)` |
| `connector.authorize` | `identity`, `connectorId?`, `idTag` | `ChargePointManager.authorize(connector, idTag)` — RFID-tap pattern |
| `connector.startTransaction` (post-MVP) | `identity`, `connectorId?`, `idTag` | `ChargePointConnectorDAO.start(idTag)` directly — plug-and-charge pattern |
| `connector.stopTransaction` | `identity`, `connectorId?`, `reason?`, `endReasonDescription?` | `ChargePointConnectorDAO.stopActiveTransactions(...)` |
| `connector.getState` | `identity`, `connectorId?` | serialize `status`, `carState`, `activeTransactionId`, `meterWh`, `locked` |

`status`/`errorCode`/`carState`/`reason` values are the exact enum names from
`ChargePointStatus`/`ChargePointErrorCode`/`CarState`/`Reason` (e.g. `"SuspendedEVSE"`,
`"NoError"`) — no remapping, so the wire format matches OCPP vocabulary test authors
likely already know.

## 8. Threading and transaction safety

No new concerns here — this reuses exactly the pattern the GUI already relies on. Every
GUI control that mutates state does so via `launchThread { ... }` (`platform/util/CoroutineExtensions.kt`),
which runs on `GlobalScope`, i.e. **off the Compose/AWT event thread already**. Exposed's
`transaction { }` blocks are safe to call from any thread (synchronized through the
HikariCP connection pool). The control socket's per-connection read loop can therefore
call the same suspend functions directly from its own coroutine — no marshalling onto a
UI thread is needed, and nothing here depends on Compose being loaded at all.

## 9. Startup wiring

- `ControlServerService` is `@Singleton` (`javax.inject.Singleton`) — auto-registered by
  Koin's existing `@ComponentScan("com.monta.ocpp.emulator")` in `MontaKoinModule`, no
  manual DI wiring needed (same as every other service in the app).
- `App.kt` parses CLI args first (`platform/util/CliArgs.kt`) and only starts the server
  when `-integration` was passed, in the same spot `databaseService.connect()` is
  called today:
  ```kotlin
  if (cliArgs.integrationMode) {
      val controlServerService: ControlServerService by injectAnywhere()
      controlServerService.start()
  }
  ```
- Shutdown: extend the existing `Runtime.getRuntime().addShutdownHook { ... }` block
  (already calls `connectionManager.disconnectAll()`) to also call
  `controlServerService.stop()` when integration mode was enabled.
- No dependency on the `application { }` Compose block — `ControlServerService.start()`
  runs regardless of whether/when the GUI window opens, so this wiring works unchanged
  even if a `--headless` mode is added later (open question, see §12).

## 10. Configuration

- Bind address: `127.0.0.1` only, not `0.0.0.0` — this is a local test-control channel,
  not a network service. Open question below: does this need to be reachable from a
  Docker container running the test suite, in which case loopback-only might not be
  enough (see §12).
- Port: needs a default (e.g. `9911`) plus an override, since a test runner may need a
  known port to connect to, or may need to avoid colliding with another local process.
  Proposed: `OCPP_EMULATOR_CONTROL_PORT` env var, falling back to the default if unset.
- No authentication in the MVP (loopback-only + local trust boundary). Flagged as an open
  question for shared/CI machines — see §12.

## 11. Concurrency

The `ServerSocket` accept loop runs on its own coroutine; each accepted connection gets
its own read loop coroutine so multiple test clients (or one client holding the
connection open across a whole test run) can issue commands concurrently. Commands
themselves are naturally serialized per charge point by whatever locking already exists
in `ConnectionManager`/`ChargePointConnection` (e.g. `connect()` already checks
`chargePointConnections[chargePointId]?.chargePoint?.connected` before doing anything) —
the control layer adds no new concurrency hazards beyond what the GUI already tolerates
today (nothing stops two GUI clicks from racing either).

## 12. Open questions (need a decision before/while implementing)

1. **Reachability**: is the test suite always co-located with the emulator process (same
   machine/container), or does the control channel need to cross a container boundary
   (e.g. CSMS test suite in one Docker container, emulator in another)? This decides
   whether `127.0.0.1`-only binding is sufficient or whether an explicit bind-address
   config and/or auth token is needed.
2. **Multiple emulator instances**: does your test setup run one emulator process
   controlling several charge points (already supported — the emulator's charge-point
   list already holds many), or does it need several emulator *processes* in parallel
   (e.g. matrix/parallel CI jobs)? If the latter, a fixed default port needs either
   per-instance override (env var, already proposed) or OS-assigned port + a discovery
   file (e.g. write the bound port to `~/monta/control.port`).
3. **Headless GUI mode**: explicitly deferred — the GUI keeps opening for now. Worth
   revisiting once the control channel exists and it's clear whether CI runners need a
   display-free run.
4. **Command catalog scope**: does §7's MVP table cover what your CSMS test suite
   actually needs to assert, or do you also need phase-2 items on day one (firmware
   status, security events, raw message injection)? Cheap to add later since they're all
   the same shape (thin dispatch to an existing function).
5. **Interceptor control** (delay/drop/edit rules): `InterceptionConfig` holds Compose
   `MutableState` (a deliberate, documented exception in this codebase — see
   `AGENTS.md`'s note on `interceptor/`). `MutableState.value` can be read/written from
   any thread without composition, so a `interceptor.configure` command is *possible*,
   but it's more design work (rule shape, matching semantics) than the other commands and
   isn't needed for the stated goal (online/offline + connector status). Recommend
   treating as a separate follow-up, not part of this plan's MVP.

## 13. Suggested implementation order

1. `control/model/` — request/response/error DTOs.
2. `control/service/ControlServerService` — socket accept/read loop, NDJSON framing,
   wired to a `ControlCommandDispatcher` interface (no commands implemented yet); one line
   in `App.kt` to start it.
3. `control/service/ControlCommandDispatcher` — implement the MVP command table (§7) one
   command at a time, each a few lines (resolve identity/connector, call the existing
   function, map exceptions to error codes).
4. `jvmTest` coverage: a test that starts `ControlServerService` against an in-memory/test
   DB, opens a real socket, sends each MVP command, asserts on the response and on the
   resulting `ChargePointDAO`/`ChargePointConnectorDAO` state — this doubles as the
   reference example for how the external test suite should talk to the socket.
5. Revisit phase-2 commands and the open questions in §12 once the MVP is in use.
