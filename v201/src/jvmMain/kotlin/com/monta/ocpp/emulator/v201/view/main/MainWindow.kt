package com.monta.ocpp.emulator.v201.view.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.monta.ocpp.emulator.common.BaseMontaWindow
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.theme.AppThemeViewModel
import com.monta.ocpp.emulator.v201.NavigationViewModel

@Preview
@Composable
fun ApplicationScope.MainWindow() {
    val appThemeViewModel: AppThemeViewModel by injectAnywhere()
    val navigationViewModel: NavigationViewModel by injectAnywhere()

    val windowState = rememberWindowState(
        size = DpSize(1200.dp, 1400.dp),
        position = WindowPosition.Aligned(
            Alignment.Center
        ),
        placement = WindowPlacement.Maximized
    )

    BaseMontaWindow(
        title = "OCPP Emulator V201",
        state = windowState,
        windowGainedFocus = {
            navigationViewModel.windowHasFocus = true
        },
        windowLostFocus = {
            navigationViewModel.windowHasFocus = false
        }
    ) {
        MaterialTheme(
            colors = appThemeViewModel.getColors()
        ) {
            when (navigationViewModel.currentScreen) {
                is NavigationViewModel.Screen.ChargePoints -> {
                }
            }
        }
    }
}
