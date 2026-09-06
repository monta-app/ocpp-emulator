package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray

/**
 * The menu-bar / system-tray icon the app lives in once its window is hidden.
 *
 * Only compose this where `isTraySupported` is true — on stock GNOME it is false, and the
 * caller then has nowhere to restore the window from.
 *
 * @param onOpen bring the hidden window back.
 */
@Composable
fun ApplicationScope.MontaTray(
    title: String,
    onOpen: () -> Unit,
) {
    Tray(
        icon = svgPainterResource("icons/tray.svg"),
        tooltip = title,
        // Left-click. macOS ignores it and always opens the menu, hence the Open item as well.
        onAction = onOpen,
        menu = {
            Item(
                text = "Open $title",
                onClick = onOpen,
            )
            Separator()
            Item(
                text = "Quit",
                onClick = ::exitApplication,
            )
        },
    )
}
