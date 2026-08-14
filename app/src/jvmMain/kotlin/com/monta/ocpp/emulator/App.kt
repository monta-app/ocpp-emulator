package com.monta.ocpp.emulator

import androidx.compose.ui.window.application
import com.monta.ocpp.emulator.control.service.ControlServerService
import com.monta.ocpp.emulator.interceptor.ui.EditMessageWindow
import com.monta.ocpp.emulator.interceptor.ui.SendMessageWindow
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.platform.analytics.service.AnalyticsHelper
import com.monta.ocpp.emulator.platform.database.service.DatabaseService
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.KoinApplication
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.startKoin
import java.util.TimeZone

private val logger = KotlinLogging.logger {}

@KoinApplication(modules = [MontaKoinModule::class])
object EmulatorApp

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    try {
        startKoin<EmulatorApp> {
            printLogger(Level.INFO)
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                val controlServerService: ControlServerService by injectAnywhere()
                controlServerService.stop()

                runBlocking {
                    val connectionManager: ConnectionManager by injectAnywhere()
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

        // Start the control socket (see docs/cli/cli-plan.md) so external test suites
        // can drive this instance without the GUI
        val controlServerService by injectAnywhere<ControlServerService>()
        controlServerService.start()

        application {
            SendMessageWindow()
            EditMessageWindow()
            MainWindow()
        }
    } catch (exception: Throwable) {
        logger.error(exception) { "app exception" }
        throw exception
    }
}
