package com.monta.ocpp.emulator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.application
import androidx.compose.ui.window.isTraySupported
import com.monta.ocpp.emulator.designsystem.ui.component.MontaTray
import com.monta.ocpp.emulator.interceptor.ui.EditMessageWindow
import com.monta.ocpp.emulator.interceptor.ui.SendMessageWindow
import com.monta.ocpp.emulator.ocpp.connection.ProtocolConnectionManager
import com.monta.ocpp.emulator.platform.analytics.service.AnalyticsHelper
import com.monta.ocpp.emulator.platform.database.service.DatabaseService
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.KoinApplication
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.startKoin
import java.util.TimeZone

private const val APP_TITLE = "OCPP Emulator"

private val logger = KotlinLogging.logger {}

@KoinApplication(modules = [MontaKoinModule::class])
object EmulatorApp

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    // Makes macOS treat the tray glyph as a template image, re-tinting it for a light or dark
    // menu bar. CTrayIcon reads this into a `static final` when it is first loaded, so it has to
    // be set before anything touches the tray.
    System.setProperty("apple.awt.enableTemplateImages", "true")
    try {
        startKoin<EmulatorApp> {
            printLogger(Level.INFO)
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                runBlocking {
                    val connectionManager: ProtocolConnectionManager by injectAnywhere()
                    connectionManager.disconnectAll()
                }
            }
        })

        // Start collecting error reports
        val analyticsHelper by injectAnywhere<AnalyticsHelper>()
        analyticsHelper.initSentry()

        // Connect to our database
        val databaseService by injectAnywhere<DatabaseService>()
        databaseService.connect()

        application {
            // Closing the main window hides it to the tray instead of quitting: the websockets
            // and schedulers in ConnectionManager are not composition-scoped, so charge points
            // keep heartbeating and charging while the UI is away.
            var mainWindowVisible by remember { mutableStateOf(true) }
            val hideToTray = { mainWindowVisible = false }

            SendMessageWindow()
            EditMessageWindow()
            MainWindow(
                visible = mainWindowVisible,
                // Without a tray to restore from — notably stock GNOME, where
                // SystemTray.isSupported() is false — closing has to keep quitting the app, or
                // the emulator would be both invisible and unquittable.
                onCloseRequest = if (isTraySupported) hideToTray else ::exitApplication,
            )

            if (isTraySupported) {
                MontaTray(
                    title = APP_TITLE,
                    onOpen = { mainWindowVisible = true },
                )
            }
        }
    } catch (exception: Throwable) {
        logger.error(exception) { "app exception" }
        throw exception
    }
}
