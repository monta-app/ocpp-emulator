package com.monta.ocpp.emulator.v201

import androidx.compose.ui.window.application
import com.monta.ocpp.emulator.CommonKoinModule
import com.monta.ocpp.emulator.v201.view.main.MainWindow
import io.github.oshai.kotlinlogging.KotlinLogging
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
        application {
            MainWindow()
        }
    } catch (exception: Throwable) {
        logger.error("app exception", exception)
        throw exception
    }
}
