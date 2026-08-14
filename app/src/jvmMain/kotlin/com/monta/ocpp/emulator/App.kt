package com.monta.ocpp.emulator

import androidx.compose.ui.window.application
import com.monta.ocpp.emulator.control.service.ControlServerService
import com.monta.ocpp.emulator.interceptor.ui.EditMessageWindow
import com.monta.ocpp.emulator.interceptor.ui.SendMessageWindow
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.platform.analytics.service.AnalyticsHelper
import com.monta.ocpp.emulator.platform.database.service.DEFAULT_DATABASE_NAME
import com.monta.ocpp.emulator.platform.database.service.DatabaseService
import com.monta.ocpp.emulator.platform.util.CliArgsException
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.platform.util.parseCliArgs
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.KoinApplication
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.startKoin
import java.util.TimeZone
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

@KoinApplication(modules = [MontaKoinModule::class])
object EmulatorApp

fun main(
    args: Array<String>,
) {
    val cliArgs = try {
        parseCliArgs(args) ?: return
    } catch (exception: CliArgsException) {
        System.err.println(exception.message)
        exitProcess(1)
    }

    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    try {
        startKoin<EmulatorApp> {
            printLogger(Level.INFO)
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                if (cliArgs.integrationMode) {
                    val controlServerService: ControlServerService by injectAnywhere()
                    controlServerService.stop()
                }

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
        val databaseService = DatabaseService(databaseName = cliArgs.databaseName ?: DEFAULT_DATABASE_NAME)
        databaseService.connect()

        // -integration: start the control socket (see docs/cli/cli-plan.md) so external
        // test suites can drive this instance without the GUI
        if (cliArgs.integrationMode) {
            val controlServerService: ControlServerService by injectAnywhere()
            controlServerService.start()
        }

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
