## Operating principles

- **Project knowledge goes in tracked Markdown, not auto-memory.** Decisions, conventions, gotchas, and status belong in checked-in `.md` files (`AGENTS.md`, `docs/`) where teammates can see them. Reserve per-user auto-memory for cross-project behavioral preferences.
- **Verify before citing.** Don't claim a library API, version, flag, or service behavior from training data — check with context7 / WebFetch / WebSearch first, even for libraries you "know."
- **The human owns the code.** No `Co-Authored-By: Claude` / `🤖 Generated with Claude Code` trailers on commits or PRs. To disclose AI assistance (optional), use a single `Assisted-by: Claude <model-id>` trailer. AI-assisted PRs are reviewed like any other; the author is accountable.

## Project

Compose for Desktop application that emulates OCPP charge points for testing CSMS backends. Kotlin Multiplatform with a single `jvm()` target; requires JDK 25 (`jvmToolchain(25)`).

There is one Gradle module (settings.gradle.kts): `:app` — the whole emulator, holding both the shared infrastructure/UI and the OCPP 1.6 protocol code. It was previously split into `:common` + `:v16`; those were merged into a single `:app` module since nothing else consumed `:common` and there was never a second protocol module. The README mentions OCPP 2.0.1 / `v201`, but that module does not exist in this repo yet.

The `com.monta.ocpp.emulator.v16` **package** (and the `ocpp-v16` library dependency) keep the `v16` name — that's the OCPP 1.6 protocol version, not a module coordinate. Only the module/directory is `app`.

## Commands

```shell
./gradlew :app:run                       # run the emulator app
./gradlew :app:jvmTest                   # run tests
./gradlew :app:jvmTest --tests "com.monta.ocpp.emulator.util.PrettyJsonFormatterTest"   # single test class
./gradlew ktlintCheck                    # lint
./gradlew ktlintFormat                   # auto-format
./gradlew :app:packageDistributionForCurrentOS   # build native installer (Dmg/Deb/Rpm/Exe)
```

CI (`.github/workflows/pull_request.yml`) runs `:app:test` (an alias for `jvmTest`) with Kover coverage, plus detekt via `monta-app/detekt-action`. Tests use JUnit 5 / kotlin-test.

Code style is enforced by ktlint (`intellij_idea` style, trailing commas required on both call and declaration sites — see `.editorconfig`). IntelliJ run configs live in `.run/`.

## Architecture

### Startup and DI (Koin 4.2 + Koin Compiler Plugin)

Entry point is `app/.../emulator/App.kt`: `main()` sets the JVM default timezone to **UTC**, starts Koin via `@KoinApplication object EmulatorApp` (a single module, `MontaKoinModule`, with `@ComponentScan("com.monta.ocpp.emulator")`), connects the database, then launches the Compose `application` with `MainWindow` plus the interceptor windows.

Keep it to **one** `@ComponentScan` over `com.monta.ocpp.emulator`. Two `@Module` classes scanning the same package in the same compilation unit register every component twice (this is why the old `CommonKoinModule` was dropped when `:common` was merged in — its scan stopped being scoped to a separate module).

DI conventions:
- Components are registered by classpath scanning with `javax.inject.Singleton` annotations and auto-bind their interfaces (e.g. each profile `*Handler` binds its `*Profile.Listener`).
- Dependencies built via builder DSLs (like `OcppClientV16`) are declared as `@Single` provider functions on `MontaKoinModule` — not classic `single { ... }` DSL. The compiler plugin's compile-safety check (KOIN-D001) misanalyzes builder-pattern lambdas by falling back to constructor analysis of the returned type, so keep builder-constructed dependencies as `@Single` provider functions.
- A class annotated `@Singleton`/`@Factory` but only constructed manually fails graph validation if its constructor params aren't in the graph (e.g. `SchedulerService` is intentionally unannotated). Use `@InjectedParam` + `parametersOf`, or drop the annotation.
- Outside the Koin/Compose graph, dependencies are pulled with the `injectAnywhere<T>()` helper (`app/.../common/util/KoinExtensions.kt`).

### OCPP protocol layer

The OCPP implementation comes from `monta-app/library-ocpp` (JitPack: `com.github.monta-app.library-ocpp:ocpp-core/ocpp-v16`). `MontaKoinModule.ocppClientV16` assembles the client from feature-profile listeners implemented in `app/.../v16/profile/` (Core, TriggerMessage, LocalAuth, SmartCharge, FirmwareManagement, Security). Connect/disconnect events go through `OcppClientEventsHandler`; every outgoing message passes through `MessageInterceptor` via `addSendHook`.

`app/.../v16/connection/ConnectionManager` is the runtime hub: it owns maps of chargePointId → `ChargePointConnection` (ktor websocket lifecycle, reconnects) and → `SchedulerService` (periodic work like heartbeats/meter values, started on connect).

### Message interception (the 🤓 button)

The `interceptor` package lets users delay/drop/edit raw OCPP messages and send arbitrary ones (`SendMessageWindow`, `EditMessageWindow` are separate top-level Compose windows registered in `App.kt`). By design these bypass the emulator's internal state machine — sending a `StopTransaction` this way does not stop the emulated charge.

### Persistence

SQLite (bundled JDBC jar in `app/libs/`) via Exposed v1 DAO API + HikariCP; the database file lives in `~/monta/`. Each domain package (`chargepoint`, `chargepointconnector`, `chargepointtransaction`, vehicle`) follows the same layering:

- `entity/` — Exposed `Table` object + `DAO` class co-located in one file
- `repository/` — query layer (wraps `transaction { }`)
- `service/` — business logic
- `view/` — Compose UI + view models

### UI

Navigation uses Jetpack Navigation Compose (JetBrains multiplatform port, `org.jetbrains.androidx.navigation:navigation-compose`). Destinations are `@Serializable` routes in `Screen` (`app/.../common/view/Screen.kt`); routes carry only serializable primitives (a screen needing a `ChargePointDAO` takes a `chargePointId` and loads the entity itself). `MainWindow` owns the `NavHost` — `ChargePoints`/`Vehicles`/`ChargePoint` are `composable<>` destinations and `CreateChargePoint` is a `dialog<>` destination (the create/edit modal). Because the `NavHostController` lives in composition but navigation is triggered from plain functions and from the separate interceptor windows (outside the NavHost), a `Navigator` Koin singleton (`app/.../common/view/Navigator.kt`) exposes intent methods (`navigate`/`navigateTopLevel`/`switchChargePoint`/`back`) over a command `Channel` that `MainWindow` collects and applies to the controller; grab it via `injectAnywhere()` like any other dependency. `Navigator` also holds `windowHasFocus` and `currentChargePointId` (tracked from the back stack) for consumers outside the NavHost. Most view models are Koin singletons holding Compose state (app-scoped); the one screen-scoped exception, `ChargePointFormViewModel` (`@Factory`), is `remember`-scoped inside the dialog so it's fresh per open. The kotlinx.serialization Gradle plugin is applied in `:app` for the `@Serializable` routes. Shared reusable components live in `app/.../common/components/`, theming in `app/.../theme/` (`MontaTheme`).

## Gotchas

- If an `app/src/main/` directory exists locally it is a stale, git-ignored leftover — real sources are under `src/jvmMain/`. Don't edit or index it.
- `ocpp-library` v3 exposes Jackson 3 (`tools.jackson`) types in its API; :app therefore depends on both Jackson 2 (bundles) and `jackson3-databind`. Keep the `jackson3` version in sync with library-ocpp.
