# PR description

**Title:** Add a remote-control socket for driving the emulator from integration tests

## Summary

- Adds an opt-in TCP control socket (`control/` package,
  `ControlServerService`/`ControlCommandDispatcher`) that lets an external test process
  connect/disconnect a charge point, plug/unplug a simulated car, force connector
  status/error, authorize/stop a transaction, and read back state — all by dispatching
  onto the same functions the GUI's own buttons already call. Speaks
  newline-delimited JSON on `127.0.0.1:9911` (overridable via `OCPP_EMULATOR_CONTROL_PORT`).
- `OcppEmulator` now takes CLI args: `-integration` starts the socket (default behavior is
  unchanged — no args, no socket), `--db <name>` points a run at an alternate SQLite file
  under `~/monta/` so integration scenarios don't clobber a developer's normal DB, `-h`/`-?`/`--help`
  prints usage and exits.
- Ships two reference/production clients in `remotecontrol/`: a pip-installable Python
  package + CLI, and a PowerShell module + CLI, both dependency-free (stdlib/.NET only),
  mirroring each other command-for-command.
- No new Gradle dependency — built on the existing `ServerSocket`, coroutines, and Jackson
  object mapper already on the classpath.
- Full design doc: `docs/cli/cli-plan.md`. Usage doc for the client tooling:
  `remotecontrol/README.md`.

## Why

CSMS teams want to drive charge point state transitions (online/offline, plug/unplug,
fault injection) from their own integration test suites, against an emulator instance that
already holds a live OCPP websocket to their backend. That live connection only exists
inside the running GUI process, so control has to happen in-process — a one-shot CLI
process or direct DB edits can't touch it. See `cli-plan.md` §2 for the two alternatives
considered (separate CLI process, embedded HTTP API) and why a loopback NDJSON socket won
out.

## What's included

- `app/src/jvmMain/kotlin/com/monta/ocpp/emulator/control/` — `model/` (request/response
  envelope + per-command params/results), `exception/`, `service/`
  (`ControlServerService` owns the socket/accept loop/framing; `ControlCommandDispatcher`
  maps command name → domain call).
- `platform/util/CliArgs.kt` (+ test) — argument parsing for `-integration`/`--db`/`--help`.
- `App.kt` — parses args first; starts/stops the control server around the existing
  connect/shutdown-hook logic when `-integration` is passed.
- `DatabaseService` — takes a `databaseName` constructor param instead of a hardcoded
  `"app.db"`, so `--db` can redirect it.
- `remotecontrol/python/` — `ocpp_emulator_remote_control` package (`ControlClient` +
  helpers) and a `remotecontrol.py` CLI.
- `remotecontrol/powershell/` — `OcppEmulatorRemoteControl` module (`.psd1`/`.psm1`) and a
  `remotecontrol.ps1` CLI.
- `docs/cli/cli-plan.md` — design doc (goal, protocol, command catalog, open questions).
- `docs/cli/cli-code-review.md` — internal review notes for this branch; documents which
  findings were fixed (bounded `disconnect` timeout, malformed-request `id` echoing, new
  `control/` test coverage) and one still open (non-atomic double-start guard on
  `ControlServerService.start()` — low risk, single call site today).

## Test coverage

New `app/src/jvmTest/kotlin/com/monta/ocpp/emulator/control/` suite added during review:
`ControlCommandDispatcherTest` (10 cases covering each dispatched command against a real
DB), `ControlServerServiceTest` (7 cases driving a real loopback socket — NDJSON framing,
malformed JSON, id-pipelining, the malformed-request/id regression case), plus
`ControlTestFixture` (shared test harness, constructor-injected, no Koin/mocking). Not
covered by design: `chargePoint.connect`/`disconnect` and `connector.authorize` need a
live/fake CSMS websocket to exercise meaningfully — out of scope for this PR.

## How to test manually

```shell
./gradlew :app:run --args="-integration --db integration-test.db"
python remotecontrol/python/remotecontrol.py hello
python remotecontrol/python/remotecontrol.py connect CP001
```

See `remotecontrol/README.md` for the full command reference and both clients' parameters.

## Out of scope / follow-ups

No authentication (loopback-only trust boundary), no headless launch mode, and the
phase-2 command catalog (firmware status, security events, raw message injection,
interceptor rule control) — all flagged as open questions in `cli-plan.md` §12, cheap to
add later since every command is the same thin-dispatch shape.
