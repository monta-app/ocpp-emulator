package com.monta.ocpp.emulator.v201

import androidx.compose.ui.window.application
import com.monta.ocpp.emulator.CommonKoinModule
import com.monta.ocpp.emulator.v201.view.main.MainWindow
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
        application {
            MainWindow()
        }
    } catch (exception: Throwable) {
        logger.error("app exception", exception)
        throw exception
    }
}
