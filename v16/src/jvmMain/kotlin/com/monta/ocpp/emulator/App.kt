package com.monta.ocpp.emulator

import androidx.compose.ui.window.application
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.interceptor.view.EditMessageWindow
import com.monta.ocpp.emulator.interceptor.view.SendMessageWindow
import com.monta.ocpp.emulator.user.AnalyticsHelper
import com.monta.ocpp.emulator.v16.connection.ConnectionManager
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.logger.Level
import org.koin.ksp.generated.module
import org.koin.mp.KoinPlatform

private val logger = KotlinLogging.logger {}

fun main() {
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

        val analyticsHelper by injectAnywhere<AnalyticsHelper>()

        analyticsHelper.initSentry()

        application {
            SendMessageWindow()
            EditMessageWindow()
            MainWindow()
        }
    } catch (exception: Throwable) {
        logger.error("app exception", exception)
        throw exception
    }
}
