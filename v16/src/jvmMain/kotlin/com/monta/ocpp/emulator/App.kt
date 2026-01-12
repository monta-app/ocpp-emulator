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
import org.koin.core.logger.Level
import org.koin.ksp.generated.module
import org.koin.mp.KoinPlatform
import java.util.TimeZone

private val logger = KotlinLogging.logger {}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    try {
        KoinPlatform.startKoin(
            listOf(
                CommonKoinModule().module,
                MontaKoinModule().module,
                MainModule.module
            ),
            Level.INFO
        )

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
