package com.monta.ocpp.emulator.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

@Composable
fun FrameWindowScope.setupAppThemeMenu(
    appThemeViewModel: AppThemeViewModel,
) {
    MenuBar {
        Menu("Theme", mnemonic = 'T') {
            Item(
                text = "Auto",
                onClick = {
                    appThemeViewModel.store(AppTheme.Auto)
                },
            )
            Item(
                "Dark",
                onClick = {
                    appThemeViewModel.store(AppTheme.Dark)
                },
            )
            Item(
                "Light",
                onClick = {
                    appThemeViewModel.store(AppTheme.Light)
                },
            )
        }
    }
}
