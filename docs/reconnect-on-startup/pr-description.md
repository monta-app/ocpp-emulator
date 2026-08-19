# PR description

**Title:** Reconnect charge points automatically on startup if previously connected

# Intent

## What should this change accomplish?

A charge point that was connected the last time the emulator ran should resume trying to
connect as soon as the emulator starts, without any user interaction.

- Done when charge points persisted with `connected = true` are handed to
  `ConnectionManager.connect` right after the database connects at startup.
- Done when, if the CSMS is unreachable at that moment, the emulator keeps retrying on its
  own (reusing the existing backoff loop) instead of requiring the user to open the charge
  point's page to kick off a connection attempt.

## Scope / non-goals

- Touches only `App.kt` (`main()`), between `databaseService.connect()` and the
  `application { }` block.
- Does not change `ConnectionManager`, `ChargePointConnection`, or the retry/backoff logic
  themselves — it only calls the existing `ConnectionManager.connect` entry point, the same
  one already used by `ChargePointPage`'s `LaunchedEffect` and the manual connect button, for
  every charge point the repository reports as `connected = true`.
- Does not change what gets persisted to `connected`, and does not add any new "was this a
  clean shutdown" tracking — it only changes when the existing state is acted upon.

## Why?

### The bug

`ConnectionManager.connect(chargePointId)` was only ever invoked from UI code:
`ChargePointPage`'s `LaunchedEffect(chargePointId)` (fires when the user opens that charge
point's page) and `ChargePointConnectionButton`'s click handler. There was no call to it
anywhere in `App.kt` / `main()`.

`chargePoint.connected` is persisted in SQLite (`ChargePointTable.connected`) and is *not*
reset on the way in — it's only ever cleared to `false` from `ChargePointConnection.disconnect()`
and `handleReconnection()`, which run as part of an orderly disconnect, or from the JVM
shutdown hook in `App.kt` calling `connectionManager.disconnectAll()`. If the process ends
any other way while a charge point is connected — killed, crashed, machine sleeps/loses
power, `disconnectAll()` doesn't get to finish because the environment doesn't grant the
shutdown hook enough time to close a websocket over the network — the row is left with
`connected = true`.

On the next launch, the UI faithfully renders that persisted state: the charge point list and
detail badges show "Connected" (`ChargePointComponent.kt`, `ChargePointTable.kt` both key off
`chargePoint.connected`). But `ConnectionManager`'s in-memory
`chargePointConnections` map starts empty every process start — there is no actual
`ChargePointConnection`, no websocket, and no retry loop behind that "Connected" badge. If the
CSMS happens to be down at that exact moment (a common ordering during local dev — e.g. the
emulator autostarts before the CSMS container is up) and later comes back, nothing in the
emulator notices: it will sit there indefinitely showing a state it isn't actually in, until a
user happens to open that charge point's page (which is what finally calls
`connectionManager.connect` and starts the real retry loop).

### Why this matters beyond one dev's machine

This is exactly the situation the emulator exists to make easy: running it alongside a CSMS
that isn't always up in the same order (containers starting on their own schedules, a backend
being restarted for a deploy, a laptop reconnecting after sleep). Every one of those is a
"previously connected, CSMS momentarily unreachable at startup" scenario, and today all of
them silently strand the charge point in a stale "Connected" state that only self-heals via
manual UI action — which is easy to not think to do, since the UI is actively telling the user
it's already connected.

## How was it built and verified?

- Traced every call site of `ConnectionManager.connect` to confirm it is currently reachable
  only from UI interaction, and traced every write to `ChargePointTable.connected` to confirm
  it is set on successful connect and cleared only on an orderly disconnect path — never
  reconciled against reality at startup.
- Fix: after `databaseService.connect()` in `main()`, fetch
  `ChargePointRepository.getConnectedChargePoints()` (a pre-existing, previously-unused-for-this
  query — `ChargePointTable.connected eq true`) and call `connectionManager.connect(...)` for
  each. This is the same call the UI already makes, so it inherits
  `ChargePointConnection`'s existing capped exponential backoff (`handleReconnection` /
  `getBackoffTime`, capped at 60s, retried indefinitely) with no new retry logic to write or
  verify separately.
- Ran `./gradlew ktlintCheck` (clean) and `./gradlew :app:jvmTest` (full suite green).
- No new automated test added: the change is a startup wiring call into an already-exercised,
  unchanged code path (`ConnectionManager.connect` / `ChargePointConnection`'s retry loop),
  and the repo has no existing harness for driving `main()` or a live websocket connection in
  tests.
- **Not verified manually against a live CSMS** — this environment has no CSMS backend to
  point the emulator at. Reviewers should sanity-check the actual scenario before merging:
  set a charge point's `connected` column to `1` in the local SQLite DB (or leave the app
  connected and force-kill it), start the emulator with the CSMS endpoint unreachable, confirm
  it starts retrying without opening the charge point's page, then bring the CSMS up and
  confirm it connects on its own.

## Context

Self-reported: hit this while running the emulator against a CSMS that starts up on its own
schedule relative to the emulator during local dev. NOJIRA — external contribution, no access
to an internal ticket for this repo.
