## Operating principles

- **Project knowledge goes in tracked Markdown, not auto-memory.** Decisions, conventions, gotchas, and status belong in checked-in `.md` files (`AGENTS.md`, `docs/`) where teammates can see them. Reserve per-user auto-memory for cross-project behavioral preferences.
- **Verify before citing.** Don't claim a library API, version, flag, or service behavior from training data — check with context7 / WebFetch / WebSearch first, even for libraries you "know."
- **The human owns the code.** No `Co-Authored-By: Claude` / `🤖 Generated with Claude Code` trailers on commits or PRs. To disclose AI assistance (optional), use a single `Assisted-by: Claude <model-id>` trailer. AI-assisted PRs are reviewed like any other; the author is accountable.

## Project

Compose for Desktop application that emulates OCPP charge points for testing CSMS backends. Kotlin Multiplatform with a single `jvm()` target; requires JDK 25 (`jvmToolchain(25)`).

There are two Gradle modules (settings.gradle.kts): `:engine` — the headless emulator (OCPP 1.6 protocol, charge-point domain, platform infrastructure, database) with no Compose dependency; and `:app` — the Compose desktop UI (window/navigation, self-updater) which depends on `:engine`. This started as a single `:app` module (itself a merge of the older `:common` + `:v16`), and `:engine` was extracted so the emulation logic can be driven without a UI. The README mentions OCPP 2.0.1 / `v201`, but that module does not exist in this repo yet.

The `com.monta.ocpp.emulator.v16` **package** (and the `ocpp-v16` library dependency) keep the `v16` name — that's the OCPP 1.6 protocol version, not a module coordinate. Only the module/directory is `app`.

## Commands

```shell
./gradlew :app:run                       # run the emulator app
./gradlew :engine:jvmTest                # run tests (the test suite lives in :engine)
./gradlew :engine:jvmTest --tests "com.monta.ocpp.emulator.platform.util.PrettyJsonFormatterTest"   # single test class
./gradlew ktlintCheck                    # lint
./gradlew ktlintFormat                   # auto-format
./gradlew :app:packageDistributionForCurrentOS   # build native installer (Dmg/Deb/Rpm/Exe)
```

CI (`.github/workflows/pull_request.yml`) runs `:engine:test` (an alias for `jvmTest`) with Kover coverage, plus detekt via `monta-app/detekt-action`. All tests currently live in `:engine`; `:app` has no test sources of its own. Tests use JUnit 5 / kotlin-test.

## Code style

ktlint (`intellij_idea` style, trailing commas required on both call and declaration sites — see `.editorconfig`) covers formatting. IntelliJ run configs live in `.run/`.

Beyond what ktlint checks, this codebase prefers **explicit over terse**. A reader scanning a call site should never have to infer a receiver's type, an implicit parameter's meaning, or where a branch ends. These four rules are **not machine-enforced** (see below) — they are review rules, so check them yourself before you call a change done:

1. **Block bodies, not expression bodies.** Always `fun f(): T { return x }`, never `fun f() = x`. Applies to functions and to DAO→DTO mappers alike.
2. **Braces on every `if`.** Never a braceless single-line body, not even for a bare `return` or `throw`. This extends to `if`/`else` used as an *expression* — brace both branches instead of `if (x) A else B`.
3. **Name every lambda parameter; never use `it`.** `.map { connector -> connector.position }`, not `.map { it.position }`. Kotest's `it("describes a case")` is the spec DSL, not a lambda parameter — leave those alone.
4. **No chained elvis fallbacks.** A single `?:` supplying a default on a nullable is fine (`connector?.kw ?: 0.0`); chaining `?:` as a lookup cascade is not. Prefer a named helper with an early `return`, or `requireNotNull`/`checkNotNull` with a message, over `a ?: b ?: error(...)`.

## Architecture

### Package layout

Both modules use the same `com/monta/ocpp/emulator/` package root, split by concern: the headless domain/protocol/platform code lives in `engine/src/jvmMain/...`, the Compose UI in `app/src/jvmMain/...`. The role folders below apply the same way in whichever module a package lives (e.g. `chargepoint/core/entity` + `repository/` + `service/` are in `:engine`, while `chargepoint/core/ui/` is in `:app`).

```
:app root:    App.kt MainWindow.kt AppKoinModule.kt   entry point, window registration, app DI module
:engine root: EngineKoinModule.kt                     engine DI module (@ComponentScan, OCPP client)
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

1. App entry point or app-side DI wiring → the `:app` root package (`App.kt`, `MainWindow.kt`, `AppKoinModule.kt`); engine-side DI wiring → the `:engine` root package (`EngineKoinModule.kt`). Nothing else lives in either root.
2. Reusable UI with no domain knowledge → `designsystem/ui/component/` or `designsystem/ui/theme/`. Navigation plumbing → `navigation/model|service/`.
3. OCPP 1.6 protocol behavior → `ocpp/v16/<role>/`.
4. App infrastructure with no domain knowledge → `platform/<concern>/<role>/`.
5. Everything else is domain or feature code → `chargepoint/<sub-aggregate>/<role>/` (the charge point itself is `core/`), `vehicle/<role>/`, `interceptor/<role>/`.

**Compose stays out of the domain, the protocol layer and the platform layer.** This is now enforced structurally: `:engine` has no Compose dependency at all, so nothing in the headless domain/protocol/platform code can import `androidx.compose`. Compose lives only in `:app` (`ui/` role folders, `App.kt`/`MainWindow.kt`). The interceptor's engine-side state (`interceptor/service/MessageInterceptor`, `interceptor/model/`) uses `MutableStateFlow` rather than Compose `MutableState`, so the app's UI observes it without the engine depending on Compose.

`ocpp/v16/` is the seam to cut along if OCPP 2.0.1 is ever added — it holds the protocol
handlers, websocket connection, scheduler and smart-charging maths, and nothing else.

### Startup and DI (Koin 4.2 + Koin Compiler Plugin)

Entry point is `App.kt`: `main()` sets the JVM default timezone to **UTC**, starts Koin via `@KoinApplication(modules = [EngineKoinModule::class, AppKoinModule::class])`, connects the database, then launches the Compose `application` with `MainWindow` plus the interceptor windows. `EngineKoinModule` (in `:engine`) registers the headless services/repositories and provides the OCPP client; `AppKoinModule` (in `:app`) registers the UI-side components.

Keep it to **one** `@ComponentScan("com.monta.ocpp.emulator")` **per module** — `EngineKoinModule` and `AppKoinModule` each scan the shared package, but KSP runs per module so each only sees its own module's sources, and no component is registered twice. The rule that bites is two `@Module` classes scanning the same package *within one module/compilation unit*: that double-registers every component (this is why the old `CommonKoinModule` was dropped when `:common` was merged in). One scan per module is correct — do not "consolidate" the two modules' scans into one.

**Closing the main window does not quit the app.** `application { }` only ends on `exitApplication()`, so `App.kt` owns a `mainWindowVisible` flag: closing hides `MainWindow` (`visible = false`) and leaves a `Tray` icon as the way back — quitting is the tray's *Quit* item or the platform quit shortcut. This works because nothing that emulates is composition-scoped: `ConnectionManager` holds its websockets and `SchedulerService`s in plain maps started via `launchThread`, so charge points keep heartbeating and charging while the UI is hidden. Two things to preserve if you touch this: hide rather than remove the window from the composition (that keeps the window position, the nav back stack and the view models alive), and keep the `isTraySupported` guard — on stock GNOME `SystemTray.isSupported()` is false, and without the fallback to `exitApplication()` the app would be both invisible and unquittable.

One `icons/tray.svg` serves every platform, and it only can because the glyph is carried by the **alpha channel** — a filled body with the bolt knocked out of it, not a second fill colour. `main()` sets `apple.awt.enableTemplateImages`, which makes AWT hand the icon to macOS as a *template*: macOS discards the colour and re-tints the remaining silhouette for a light or dark menu bar, so anything expressed as a lighter fill would flatten into a solid blob, while a knockout survives. Windows and Linux draw the same file as-is. `CTrayIcon` reads that property into a `static final` at class-load, so it has to be set before anything touches the tray.

DI conventions:
- Components are registered by classpath scanning with `javax.inject.Singleton` annotations and auto-bind their interfaces (e.g. each profile `*Handler` binds its `*Profile.Listener`).
- Dependencies built via builder DSLs (like `OcppClientV16`) are declared as `@Single` provider functions on `EngineKoinModule` — not classic `single { ... }` DSL. The compiler plugin's compile-safety check (KOIN-D001) misanalyzes builder-pattern lambdas by falling back to constructor analysis of the returned type, so keep builder-constructed dependencies as `@Single` provider functions.
- A class annotated `@Singleton`/`@Factory` but only constructed manually fails graph validation if its constructor params aren't in the graph (e.g. `SchedulerService` is intentionally unannotated). Use `@InjectedParam` + `parametersOf`, or drop the annotation.
- Outside the Koin/Compose graph, dependencies are pulled with the `injectAnywhere<T>()` helper (`platform/util/KoinExtensions.kt`).

### OCPP protocol layer

The OCPP implementation comes from `monta-app/library-ocpp` (JitPack: `com.github.monta-app.library-ocpp:ocpp-core/ocpp-v16`). `EngineKoinModule.ocppClientV16` assembles the client from feature-profile listeners implemented in `ocpp/v16/profile/` (Core, TriggerMessage, LocalAuth, SmartCharge, FirmwareManagement, Security). Connect/disconnect events go through `OcppClientEventsHandler`; every outgoing message passes through `MessageInterceptor` via `addSendHook`.

`ocpp/v16/connection/ConnectionManager` is the runtime hub: it owns maps of chargePointId → `ChargePointConnection` (ktor websocket lifecycle, reconnects) and → `SchedulerService` (periodic work like heartbeats/meter values, started on connect).

### Message interception (the 🤓 button)

The `interceptor` package lets users delay/drop/edit raw OCPP messages and send arbitrary ones (`SendMessageWindow`, `EditMessageWindow` are separate top-level Compose windows registered in `App.kt`). By design these bypass the emulator's internal state machine — sending a `StopTransaction` this way does not stop the emulated charge.

### Persistence

SQLite (bundled JDBC jar in `engine/libs/`) via Exposed v1 DAO API + HikariCP; the database file lives in `~/monta/`. Setup is split across `platform/database/`: `service/DatabaseInitiator` (Hikari + Exposed wiring), `service/DatabaseService` (schema creation — **register new tables here**), `extension/DatabaseExtensions` (entity-hook `Flow`s that drive UI recomposition).

Every persisted aggregate (`chargepoint/core`, `chargepoint/connector`, `chargepoint/transaction`, `chargepoint/txdefault`, `platform/config`) uses the same role folders (`vehicle` is not persisted — it has no entities, just `model/` + `service/`):

- `entity/` — Exposed `Table` object + `DAO` class co-located in one file
- `repository/` — query layer (wraps `transaction { }`)
- `service/` — business logic

Persisted JSON columns (`platform/util/JsonColumnType.kt`) use plain property-based Jackson with no default typing, so no fully-qualified class names are written to the database — classes can be moved between packages without breaking existing local databases.

### UI

Navigation uses Jetpack Navigation Compose (JetBrains multiplatform port, `org.jetbrains.androidx.navigation:navigation-compose`). Destinations are `@Serializable` routes in `Screen` (`navigation/model/Screen.kt`); routes carry only serializable primitives (a screen needing a `ChargePointDAO` takes a `chargePointId` and loads the entity itself). `MainWindow` owns the `NavHost` — `ChargePoints`/`Vehicles`/`ChargePoint` are `composable<>` destinations and `CreateChargePoint` is a `dialog<>` destination (the create/edit modal). Because the `NavHostController` lives in composition but navigation is triggered from plain functions and from the separate interceptor windows (outside the NavHost), a `Navigator` Koin singleton (`navigation/service/Navigator.kt`) exposes intent methods (`navigate`/`navigateTopLevel`/`switchChargePoint`/`back`) over a command `Channel` that `MainWindow` collects and applies to the controller; grab it via `injectAnywhere()` like any other dependency. `Navigator` also holds `windowHasFocus` and `currentChargePointId` (tracked from the back stack) for consumers outside the NavHost. Most view models are Koin singletons holding Compose state (app-scoped); the one screen-scoped exception, `ChargePointFormViewModel` (`@Factory`), is `remember`-scoped inside the dialog so it's fresh per open. The kotlinx.serialization Gradle plugin is applied in `:app` for the `@Serializable` routes. Shared reusable components live in `designsystem/ui/component/`, theming in `designsystem/ui/theme/` (`MontaTheme`). Charge-point UI is grouped by screen — `chargepoint/core/ui/list`, `/detail`, `/form`, `/pbm`, `/security` — with `chargepoint/core/ui/component/` holding only the pieces shared across more than one of those; connector widgets live with their sub-aggregate in `chargepoint/connector/ui/`.

## Gotchas

- If an `app/src/main/` directory exists locally it is a stale, git-ignored leftover — real sources are under `src/jvmMain/`. Don't edit or index it.
- `ocpp-library` v3 exposes Jackson 3 (`tools.jackson`) types in its API; :engine (and :app, which still sends OCPP messages directly) therefore depends on both Jackson 2 (bundles) and `jackson3-databind`. Keep the `jackson3` version in sync with library-ocpp.
