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

### Package layout

Everything lives under `app/src/jvmMain/kotlin/com/monta/ocpp/emulator/`:

```
App.kt MainWindow.kt MontaKoinModule.kt   entry point, window registration, DI module
chargepoint/        the domain aggregate — charge point → connector → transaction
  core/             the aggregate root's own layers — model/ entity/ repository/ service/ exception/
                    ui/ (grouped by screen: list/ detail/ form/ pbm/ security/ + shared component/)
  connector/        entity/ model/ repository/ service/ ui/
  transaction/      entity/ repository/ service/
  txdefault/        entity/ repository/ service/
vehicle/            model/ service/ ui/
interceptor/        the 🤓 message-interception feature — model/ service/ ui/
ocpp/v16/           the OCPP 1.6 protocol adapter — the only version-specific code
  service/ profile/ connection/ scheduler/ smartcharging/ extension/
designsystem/       ui/component/ (reusable widgets) + ui/theme/ (MontaTheme, MontaColors)
navigation/         model/ (Screen routes) + service/ (Navigator)
platform/           app infrastructure, nothing domain-specific — one subpackage per concern,
                    each with role folders: analytics/ config/ database/ eichrecht/ logging/ update/ util/
```

**The one structural rule — no exceptions:** every `.kt` file lives inside a *role folder*; packages above role folders only namespace. A package's direct children are either all role folders or all sub-packages (sub-aggregates / concerns) — never a mix. When a package has sub-packages, its own code goes in `core/` (see `chargepoint/core`). The role vocabulary is fixed:

- `model/` — plain types and enums
- `entity/` — Exposed `Table` + `DAO` (persisted aggregates only)
- `repository/` — query layer
- `service/` — business logic, orchestrators, handlers
- `exception/` — domain exceptions
- `extension/` — extension functions
- `util/` — role-less helpers (only under `platform/`)
- `ui/` — Compose code, grouped by screen (`list/`, `detail/`, `form/`, …); `ui/component/` holds only pieces shared by ≥ 2 screens; ViewModels live with their screen
- `ocpp/v16` additionally uses protocol-concept folders (`profile/`, `connection/`, `scheduler/`, `smartcharging/`) as its role vocabulary — an adapter speaks the protocol's language

Role folders repeat at every level even when they hold a single file — predictability beats flatness. **A file's declared package always matches its directory.** The pre-`:app` tree had files in `chargepoint/` declaring `v16.data.*`, which made the tree and the imports disagree; don't reintroduce that.

**Where does a new file go?**

1. App entry point or DI wiring → the root package (`App.kt`, `MainWindow.kt`, `MontaKoinModule.kt`). Nothing else lives there.
2. Reusable UI with no domain knowledge → `designsystem/ui/component/` or `designsystem/ui/theme/`. Navigation plumbing → `navigation/model|service/`.
3. OCPP 1.6 protocol behavior → `ocpp/v16/<role>/`.
4. App infrastructure with no domain knowledge → `platform/<concern>/<role>/`.
5. Everything else is domain or feature code → `chargepoint/<sub-aggregate>/<role>/` (the charge point itself is `core/`), `vehicle/<role>/`, `interceptor/<role>/`.

**Compose stays out of the domain, the protocol layer and the platform layer.** Verified: nothing outside `ui/` role folders, `App.kt`/`MainWindow.kt` and the `interceptor` feature imports `androidx.compose`. Keep it that way — it's what makes the domain readable without a UI in your head. (`interceptor/` is the deliberate exception: `interceptor/service/MessageInterceptor` and `interceptor/model/` hold Compose state directly, because the interception rules *are* UI state.)

`ocpp/v16/` is the seam to cut along if OCPP 2.0.1 is ever added — it holds the protocol
handlers, websocket connection, scheduler and smart-charging maths, and nothing else.

### Startup and DI (Koin 4.2 + Koin Compiler Plugin)

Entry point is `App.kt`: `main()` sets the JVM default timezone to **UTC**, starts Koin via `@KoinApplication object EmulatorApp` (a single module, `MontaKoinModule`, with `@ComponentScan("com.monta.ocpp.emulator")`), connects the database, then launches the Compose `application` with `MainWindow` plus the interceptor windows.

Keep it to **one** `@ComponentScan` over `com.monta.ocpp.emulator`. Two `@Module` classes scanning the same package in the same compilation unit register every component twice (this is why the old `CommonKoinModule` was dropped when `:common` was merged in — its scan stopped being scoped to a separate module).

DI conventions:
- Components are registered by classpath scanning with `javax.inject.Singleton` annotations and auto-bind their interfaces (e.g. each profile `*Handler` binds its `*Profile.Listener`).
- Dependencies built via builder DSLs (like `OcppClientV16`) are declared as `@Single` provider functions on `MontaKoinModule` — not classic `single { ... }` DSL. The compiler plugin's compile-safety check (KOIN-D001) misanalyzes builder-pattern lambdas by falling back to constructor analysis of the returned type, so keep builder-constructed dependencies as `@Single` provider functions.
- A class annotated `@Singleton`/`@Factory` but only constructed manually fails graph validation if its constructor params aren't in the graph (e.g. `SchedulerService` is intentionally unannotated). Use `@InjectedParam` + `parametersOf`, or drop the annotation.
- Outside the Koin/Compose graph, dependencies are pulled with the `injectAnywhere<T>()` helper (`platform/util/KoinExtensions.kt`).

### OCPP protocol layer

The OCPP implementation comes from `monta-app/library-ocpp` (JitPack: `com.github.monta-app.library-ocpp:ocpp-core/ocpp-v16`). `MontaKoinModule.ocppClientV16` assembles the client from feature-profile listeners implemented in `ocpp/v16/profile/` (Core, TriggerMessage, LocalAuth, SmartCharge, FirmwareManagement, Security). Connect/disconnect events go through `OcppClientEventsHandler`; every outgoing message passes through `MessageInterceptor` via `addSendHook`.

`ocpp/v16/connection/ConnectionManager` is the runtime hub: it owns maps of chargePointId → `ChargePointConnection` (ktor websocket lifecycle, reconnects) and → `SchedulerService` (periodic work like heartbeats/meter values, started on connect).

### Message interception (the 🤓 button)

The `interceptor` package lets users delay/drop/edit raw OCPP messages and send arbitrary ones (`SendMessageWindow`, `EditMessageWindow` are separate top-level Compose windows registered in `App.kt`). By design these bypass the emulator's internal state machine — sending a `StopTransaction` this way does not stop the emulated charge.

### Persistence

SQLite (bundled JDBC jar in `app/libs/`) via Exposed v1 DAO API + HikariCP; the database file lives in `~/monta/`. Setup is split across `platform/database/`: `service/DatabaseInitiator` (Hikari + Exposed wiring), `service/DatabaseService` (schema creation — **register new tables here**), `extension/DatabaseExtensions` (entity-hook `Flow`s that drive UI recomposition).

Every persisted aggregate (`chargepoint/core`, `chargepoint/connector`, `chargepoint/transaction`, `chargepoint/txdefault`, `platform/config`) uses the same role folders (`vehicle` is not persisted — it has no entities, just `model/` + `service/`):

- `entity/` — Exposed `Table` object + `DAO` class co-located in one file
- `repository/` — query layer (wraps `transaction { }`)
- `service/` — business logic

Persisted JSON columns (`platform/util/JsonColumnType.kt`) use plain property-based Jackson with no default typing, so no fully-qualified class names are written to the database — classes can be moved between packages without breaking existing local databases.

### UI

Navigation uses Jetpack Navigation Compose (JetBrains multiplatform port, `org.jetbrains.androidx.navigation:navigation-compose`). Destinations are `@Serializable` routes in `Screen` (`navigation/model/Screen.kt`); routes carry only serializable primitives (a screen needing a `ChargePointDAO` takes a `chargePointId` and loads the entity itself). `MainWindow` owns the `NavHost` — `ChargePoints`/`Vehicles`/`ChargePoint` are `composable<>` destinations and `CreateChargePoint` is a `dialog<>` destination (the create/edit modal). Because the `NavHostController` lives in composition but navigation is triggered from plain functions and from the separate interceptor windows (outside the NavHost), a `Navigator` Koin singleton (`navigation/service/Navigator.kt`) exposes intent methods (`navigate`/`navigateTopLevel`/`switchChargePoint`/`back`) over a command `Channel` that `MainWindow` collects and applies to the controller; grab it via `injectAnywhere()` like any other dependency. `Navigator` also holds `windowHasFocus` and `currentChargePointId` (tracked from the back stack) for consumers outside the NavHost. Most view models are Koin singletons holding Compose state (app-scoped); the one screen-scoped exception, `ChargePointFormViewModel` (`@Factory`), is `remember`-scoped inside the dialog so it's fresh per open. The kotlinx.serialization Gradle plugin is applied in `:app` for the `@Serializable` routes. Shared reusable components live in `designsystem/ui/component/`, theming in `designsystem/ui/theme/` (`MontaTheme`). Charge-point UI is grouped by screen — `chargepoint/core/ui/list`, `/detail`, `/form`, `/pbm`, `/security` — with `chargepoint/core/ui/component/` holding only the pieces shared across more than one of those; connector widgets live with their sub-aggregate in `chargepoint/connector/ui/`.

## Gotchas

- If an `app/src/main/` directory exists locally it is a stale, git-ignored leftover — real sources are under `src/jvmMain/`. Don't edit or index it.
- `ocpp-library` v3 exposes Jackson 3 (`tools.jackson`) types in its API; :app therefore depends on both Jackson 2 (bundles) and `jackson3-databind`. Keep the `jackson3` version in sync with library-ocpp.
