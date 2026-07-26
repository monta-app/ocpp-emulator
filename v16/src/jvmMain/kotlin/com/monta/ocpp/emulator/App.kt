package com.monta.ocpp.emulator

import androidx.compose.ui.window.application
import com.monta.ocpp.emulator.common.DatabaseService
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.interceptor.view.EditMessageWindow
import com.monta.ocpp.emulator.interceptor.view.SendMessageWindow
import com.monta.ocpp.emulator.user.AnalyticsHelper
import com.monta.ocpp.emulator.v16.connection.ConnectionManager
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
