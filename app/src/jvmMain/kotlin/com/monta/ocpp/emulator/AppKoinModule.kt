package com.monta.ocpp.emulator

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin module for the Compose desktop UI. Its component scan runs against the app's own sources
 * (KSP is per module), so it registers the app-side `@Single`/`@Singleton` classes — view models,
 * the navigator, the theme view model, the self-updater and the edit-message prompt implementation.
 *
 * The engine-side definitions live in [EngineKoinModule]; `App.kt` loads both modules together.
 */
@Module
@ComponentScan("com.monta.ocpp.emulator")
class AppKoinModule
