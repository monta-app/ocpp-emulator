# Code review — remote-control (CLI) branch

Review of `feature/cli` against `main` (the control socket, `CliArgs`, and the
Python/PowerShell reference clients). Findings ranked most-severe first.
Verified by reading the diff and enclosing functions, compiling `:app:compileTestKotlinJvm`,
running `ktlintCheck` and `:app:jvmTest`, and empirically checking Jackson's
null-deserialization behavior for `ControlCommandDispatcher.bind()`.

**Status:** #1, #2, and #3 are fixed (see "Fix applied"/"Tests added" under each). #4 is still open.

## 1. `chargePoint.disconnect` has no bounded timeout, unlike `chargePoint.connect` — FIXED

**File:** `app/src/jvmMain/kotlin/com/monta/ocpp/emulator/control/service/ControlCommandDispatcher.kt:504`

```kotlin
connectionManager.disconnect(chargePoint.idValue)?.join()
```

`chargePoint.connect` is explicitly bounded — `awaitConnected()` polls for at most
`CONNECT_TIMEOUT_MILLIS` (10s) so a charge point that never reaches the CSMS doesn't hang
the control connection. `disconnect` has no equivalent bound: it `.join()`s the
`ConnectionManager.disconnect()` job unconditionally.

`ChargePointConnection.disconnect()` (`ocpp/v16/connection/ChargePointConnection.kt:197-223`)
sends a `StatusNotificationRequest` over the live OCPP websocket *before* closing it. If the
CSMS is slow or unreachable, that send can block well past 10 seconds.

Both reference clients size their socket read timeout off the *documented* 10s connect
bound:
- Python `ControlClient.__init__` (`remotecontrol/python/ocpp_emulator_remote_control/client.py:1851-1855`): `timeout: float = 15.0`
- PowerShell `New-OcppControlClient` (`remotecontrol/powershell/OcppEmulatorRemoteControl.psm1:1356`): `[int]$TimeoutSec = 15`

A slow/unreachable CSMS during disconnect can exceed that 15s client-side timeout, so the
control client sees a spurious connection-timeout/failure even though the emulator
eventually completes the disconnect server-side.

**Suggested fix:** give `disconnect()` the same kind of bounded wait `connect()` has (e.g.
`withTimeoutOrNull(CONNECT_TIMEOUT_MILLIS) { job?.join() }`), and document the bound the
same way `awaitConnected()` is documented.

**Fix applied:** `ControlCommandDispatcher.disconnect()` now wraps the `.join()` in
`withTimeoutOrNull(DISCONNECT_TIMEOUT_MILLIS)` (a new 10s constant, matching
`CONNECT_TIMEOUT_MILLIS`). If the deadline passes, dispatch stops waiting and returns
whatever `ChargePointDAO` state is currently persisted, instead of hanging the control
connection indefinitely.

## 2. Malformed request loses the caller's `id`, breaking the documented pipelining contract — FIXED

**File:** `app/src/jvmMain/kotlin/com/monta/ocpp/emulator/control/service/ControlServerService.kt:740-751`

```kotlin
private fun handleLine(line: String): ControlResponse {
    val request = try {
        objectMapper.readValue(line, ControlRequest::class.java)
    } catch (exception: Exception) {
        return ControlResponse.failure(
            id = null,
            code = "INVALID_REQUEST",
            message = exception.message ?: "malformed request",
        )
    }
    ...
}
```

`id` is hardcoded to `null` whenever deserialization throws — including when the JSON is
otherwise well-formed but merely missing the required `command` field, e.g.
`{"id":"7","params":{}}`. Jackson fails to construct `ControlRequest` (`command: String`
has no default) before the `id` value can be read out of it, so the caller's `id` is
discarded.

This directly contradicts the protocol contract documented in `docs/cli/cli-plan.md` §7:

> `id` is caller-supplied and echoed back unchanged, so a test client can pipeline several
> commands on one connection and match responses to requests.

A client pipelining multiple in-flight requests on one connection cannot correlate this
failure response back to the request that caused it.

**Suggested fix:** parse the JSON generically first (`objectMapper.readTree(line)`), pull
`id` out of the raw tree before attempting to bind to `ControlRequest`, and use that `id` in
the failure response regardless of where deserialization failed.

**Fix applied:** `ControlServerService.handleLine()` now parses the line with
`objectMapper.readTree(line)` first, extracts `id` from the raw tree (`null` if absent or
JSON `null`), and uses that `id` in the `INVALID_REQUEST` response if the subsequent
`objectMapper.treeToValue(tree, ControlRequest::class.java)` binding fails (e.g. a missing
`command` field). Only genuinely unparsable JSON (the `readTree` call itself throwing)
still reports `id = null`, since there's no tree to pull an id out of in that case.

## 3. New `control/` subsystem has no automated test coverage — FIXED

**Files:** `app/src/jvmMain/kotlin/com/monta/ocpp/emulator/control/service/ControlCommandDispatcher.kt`,
`app/src/jvmMain/kotlin/com/monta/ocpp/emulator/control/service/ControlServerService.kt`

The diff's own design doc plans this work — `docs/cli/cli-plan.md` §13 step 4:

> `jvmTest` coverage: a test that starts `ControlServerService` against an in-memory/test
> DB, opens a real socket, sends each MVP command, asserts on the response and on the
> resulting `ChargePointDAO`/`ChargePointConnectorDAO` state — this doubles as the
> reference example for how the external test suite should talk to the socket.

The only new test in the diff is `app/src/jvmTest/kotlin/com/monta/ocpp/emulator/platform/util/CliArgsTest.kt`
(CLI argument parsing). None of the 9 dispatched commands, the NDJSON framing, or the
params/result DTO (de)serialization in `ControlCommandDispatcher`/`ControlServerService` are
covered by `:app:jvmTest`, so CI would not catch a regression in any of them.

**Suggested fix:** add the socket-level integration test §13 already calls for, at minimum
covering the malformed-request/`id` case (finding 2) and a full round trip per command.

**Tests added:**

- `app/src/jvmTest/kotlin/com/monta/ocpp/emulator/control/service/ControlTestFixture.kt` —
  shared, lazily-initialized `object` fixture that builds a real
  `ChargePointService`/`ConnectionManager`/`ChargePointManager`/`ControlCommandDispatcher`
  graph by hand (constructor injection, no Koin — this project has no test mocking library)
  against a throwaway SQLite database under `~/monta/` (unique per test run, deleted via a
  JVM shutdown hook). It's a single shared `object` specifically so only *one*
  `DatabaseService.connect()` call happens for the whole `control/` test suite — Exposed's
  no-arg `transaction { }` follows whichever `Database` was connected *most recently*, so two
  independent `connect()` calls from two test classes would have made the second one silently
  steal the "current" database out from under the first.
- `app/src/jvmTest/kotlin/com/monta/ocpp/emulator/control/service/ControlCommandDispatcherTest.kt`
  — 10 tests against `ControlCommandDispatcher.dispatch()` directly: `hello`, unknown-command →
  `UNKNOWN_COMMAND`, a params object missing a required field → `INVALID_PARAMS`, an unknown
  identity → `CHARGE_POINT_NOT_FOUND`, `chargePoint.getState`/`connector.getState` (including
  connector self-heal) against seeded data, and that `chargePoint.setAvailability`,
  `connector.setCarState`, `connector.setStatus`, and `connector.stopTransaction` actually
  persist their changes.
- `app/src/jvmTest/kotlin/com/monta/ocpp/emulator/control/service/ControlServerServiceTest.kt`
  — 7 tests driving a real loopback `Socket` against a real `ControlServerService`: a `hello`
  round trip through actual NDJSON, the malformed-JSON case, **the regression case for finding
  2** (well-formed JSON missing `command` still echoes the caller's `id`), unknown-command,
  a request with no `id` gets a response with no `id`, blank lines between commands being
  ignored, and one connection pipelining several commands and matching responses by `id`.

**Enabled by a small testability addition to `ControlServerService`:** `start()` now takes an
optional `port` parameter (default unchanged: the `OCPP_EMULATOR_CONTROL_PORT` env var, else
`9911`), and a new `boundPort: Int?` property exposes the actually-bound port. Tests call
`start(port = 0)` to get an OS-assigned ephemeral port (avoiding collisions with a real running
instance or other test runs) and read it back via `boundPort`.

Deliberately still out of scope (documented in `ControlTestFixture`'s doc comment): `chargePoint.connect`/`disconnect`
and `connector.authorize`, which need a live (or fake) OCPP websocket/CSMS — and the actual
timeout behavior added for finding 1, which would need a slow/fake CSMS endpoint to trigger.

## 4. `ControlServerService.start()` double-start check is not atomic

**File:** `app/src/jvmMain/kotlin/com/monta/ocpp/emulator/control/service/ControlServerService.kt:658-678`

```kotlin
fun start() {
    if (serverSocket != null) {
        return
    }
    ...
    serverSocket = socket
    ...
}
```

`serverSocket` is `@Volatile` but the `if (serverSocket != null) return` guard is a
check-then-act race, not a compare-and-set. `ControlServerService` is a Koin singleton
reachable from anywhere via `injectAnywhere()`, not just the one call site in `App.kt`.

**Failure scenario:** two concurrent calls to `start()` can both observe `serverSocket ==
null`, and both proceed to `ServerSocket().bind(...)` on the same port; the second bind
throws `BindException`, which is uncaught by `start()` and propagates to the caller. Low
likelihood given the current single call site, but cheap to close off (e.g. a
`synchronized` block or `AtomicReference.compareAndSet`).

---

### Not flagged (checked and ruled out)

- **`settings.gradle.kts`** — the new `plugins {}` block is placed *after*
  `rootProject.name`/`include("app")`, which looked like it would violate Gradle's
  "plugins block must be first" rule. Verified empirically: `./gradlew help --offline`
  builds successfully, so this is not a real issue with the Gradle/Kotlin-DSL version in
  use here.
- **`DatabaseService` losing its `@Singleton`/DI wiring** — `App.kt` is now the only
  consumer (`grep` confirmed no other `injectAnywhere<DatabaseService>()` call sites), and
  `:app:compileTestKotlinJvm` (with Koin's `strictSafety` graph validation) passes cleanly.
- **`ControlCommandDispatcher.bind()` returning null params for a `NullNode` (missing
  `params`) causing a raw NPE** — empirically verified via a throwaway test: Jackson's
  Kotlin-module null-assertion on the `bind()` return statement throws inside the
  function's own `try`, so it's caught and correctly reported as `INVALID_PARAMS` (just
  with a slightly cryptic message), not misreported as `INTERNAL_ERROR`.
- **`ChargePointConnectorDAO.setStatus`'s `this@setStatus.statusInfo = statusInfo`**
  (`ocpp/v16/extension/ChargePointConnectorExtensions.kt:51`) is a genuine self-assignment
  bug (the `info` parameter is never persisted to the `statusInfo` column), but it is
  pre-existing, untouched by this diff, and not observably broken by the new feature: the
  live `StatusNotification` sent to the CSMS still carries the correct `info` value, and
  `ConnectorStateResult` doesn't expose `statusInfo` back through the control API anyway.
