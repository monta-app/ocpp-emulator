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
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.configuration.AppConfigService
import com.monta.ocpp.emulator.logger.ChargePointLogger
import javax.inject.Singleton

@Singleton
class AppThemeViewModel(
    private val appConfigService: AppConfigService,
) {

    companion object {
        private const val APP_THEME_KEY = "app_theme"
    }

    var appTheme by mutableStateOf(
        AppTheme.parse(appConfigService.getByKey(APP_THEME_KEY)),
    )

    fun store(
        appTheme: AppTheme,
    ) {
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
            // On dark surfaces the signature Primary Blue reads too dark for
            // text/accents, so we lead with Light Blue and keep Primary Blue as
            // the variant. Both stay in the cold family, so the palette holds.
            darkColors(
                primary = MontaColors.LightBlue,
                primaryVariant = MontaColors.PrimaryBlue,
                secondary = MontaColors.LightBlue,
                secondaryVariant = MontaColors.PrimaryBlue,
                onPrimary = MontaColors.White,
                onSecondary = MontaColors.White,
                onBackground = MontaColors.White,
                onSurface = MontaColors.White,
                onError = MontaColors.White,
            )
        } else {
            // Primary Blue leads (top app bar, buttons); Light Blue is the
            // secondary accent used sparingly.
            lightColors(
                primary = MontaColors.PrimaryBlue,
                primaryVariant = MontaColors.DarkBlue,
                secondary = MontaColors.LightBlue,
                secondaryVariant = MontaColors.PrimaryBlue,
                background = MontaColors.GreyBackground,
                surface = MontaColors.White,
                onPrimary = MontaColors.White,
                onSecondary = MontaColors.White,
                onBackground = MontaColors.Black,
                onSurface = MontaColors.Black,
                onError = MontaColors.White,
            )
        }
    }

    @Composable
    fun getLogColors(
        logItem: ChargePointLogger.LogEntry,
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
