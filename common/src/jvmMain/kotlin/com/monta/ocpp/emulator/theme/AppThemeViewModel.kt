package com.monta.ocpp.emulator.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.monta.ocpp.emulator.common.components.toColor
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.configuration.AppConfigService
import com.monta.ocpp.emulator.logger.ChargePointLogger
import javax.inject.Singleton

@Singleton
class AppThemeViewModel(
    private val appConfigService: AppConfigService
) {

    companion object {
        private const val APP_THEME_KEY = "app_theme"
    }

    var appTheme by mutableStateOf(
        AppTheme.parse(appConfigService.getByKey(APP_THEME_KEY))
    )

    fun store(appTheme: AppTheme) {
        launchThread {
            appConfigService.upsert(APP_THEME_KEY, appTheme.name)
        }
        this.appTheme = appTheme
    }

    @Composable
    fun isDarkMode(): Boolean {
        return when (appTheme) {
            AppTheme.Auto -> isSystemInDarkTheme()
            AppTheme.Light -> false
            AppTheme.Dark -> true
        }
    }

    @Composable
    fun getColors(): Colors {
        return if (isDarkMode()) {
            darkColors(
                primary = "#FF5252".toColor(),
                primaryVariant = "#FF3838".toColor(),
                secondary = "#5E72E4".toColor(),
                secondaryVariant = "#4255D9".toColor(),
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = Color.White,
                onSurface = Color.White,
                onError = Color.White
            )
        } else {
            lightColors(
                primary = "#FF5252".toColor(),
                secondary = "#5e72e4".toColor(),
                background = "#E3E6E9".toColor(),
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = Color.Black,
                onSurface = Color.Black,
                onError = Color.White
            )
        }
    }

    @Composable
    fun getLogColors(
        logItem: ChargePointLogger.LogEntry
    ): Color {
        return if (isDarkMode()) {
            when (logItem.level) {
                ChargePointLogger.Level.Error -> Color(204, 0, 0)
                ChargePointLogger.Level.Warn -> Color(196, 160, 0)
                ChargePointLogger.Level.Info -> Color.White
                ChargePointLogger.Level.Debug -> Color(78, 154, 6)
                ChargePointLogger.Level.Trace -> Color(114, 159, 207)
            }
        } else {
            when (logItem.level) {
                ChargePointLogger.Level.Error -> Color(204, 0, 0)
                ChargePointLogger.Level.Warn -> Color(196, 160, 0)
                ChargePointLogger.Level.Info -> Color.Black
                ChargePointLogger.Level.Debug -> Color(78, 154, 6)
                ChargePointLogger.Level.Trace -> Color(114, 159, 207)
            }
        }
    }
}
