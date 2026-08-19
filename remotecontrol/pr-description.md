# OcppEmulator Remote Control for integration testing scenarios

## What should this change accomplish?

Let an external test process drive a **running** emulator instance — connect/disconnect
a charge point, plug/unplug a simulated car, force connector status/error, authorize or
start/stop a transaction — without clicking through the GUI, by dispatching onto the same
functions the GUI's own buttons already call.

- Done when `OcppEmulator -integration` opens a loopback NDJSON socket (default port 9911,
  overridable via `OCPP_EMULATOR_CONTROL_PORT`) exposing the command catalog in
  `remotecontrol/README.md`; plain `OcppEmulator` (no args) behaves exactly as before.
- Done when `--db <name>` lets an `-integration` run point at an alternate SQLite file
  under `~/monta/`, so integration scenarios don't clobber a developer's normal DB.
- Done when `-h`/`-?`/`--help` prints usage and exits without starting anything.
- Done when both reference clients (`remotecontrol/python`, `remotecontrol/powershell`)
  can issue every command in the catalog against a running instance and get back a
  matching response, with no new third-party dependency in either.

## Scope / non-goals

Touches: new `control/` package (`model/`, `exception/`, `service/` —
`ControlServerService` + `ControlCommandDispatcher`), `platform/util/CliArgs.kt`, the
`App.kt` startup wiring, `DatabaseService`'s constructor (takes `databaseName` instead of
a hardcoded `"app.db"`), a doc comment on `ConnectionManager.connect()`, and
`windows { console = true }` in `app/build.gradle.kts` (so `--help`/CLI errors have a
console to print to — trade-off: every normal GUI launch now also opens a console window;
called out in `remotecontrol/README.md` as undecided). Two new standalone client packages
under `remotecontrol/`.

Deliberately not included: authentication (loopback-only trust boundary), a headless
launch mode, and the phase-2 command catalog (firmware status, security events, raw
message injection, interceptor rule control) — see `remotecontrol-plan.md` §12.

## Why?

CSMS teams want to script charge point state transitions (online/offline, plug/unplug,
fault injection, transaction start/stop) from their own integration test suites, against
an emulator that already holds a live OCPP websocket to their backend. That connection
only exists inside the running GUI process, so control has to happen in-process — a
separate one-shot CLI process or direct DB edits can't touch it and would desync the DB
from reality.

## How was it built and verified?

Built on the existing `ServerSocket`, coroutines, and Jackson object mapper already on
the classpath — no new Gradle dependency for the control socket itself. Read the full
diff and confirmed it matches the intent above.

Ran `./gradlew ktlintCheck` and `./gradlew :app:jvmTest` — both pass, including the new
`control/` suite (`ControlCommandDispatcherTest`, `ControlServerServiceTest`,
`ControlTestFixture`) and `CliArgsTest`.

Manually verified with `./gradlew :app:run --args="-integration --db integration-test.db"`
plus both `remotecontrol/python/remotecontrol.py` and `remotecontrol/powershell/remotecontrol.ps1`
CLIs against [fill in: which CSMS backend / local mock you tested against]. `chargePoint.connect`/
`disconnect` and `connector.authorize` aren't covered by the automated suite by design —
they need a live/fake CSMS websocket — so this manual pass is their only verification.

