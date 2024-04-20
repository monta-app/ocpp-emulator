package com.monta.ocpp.emulator.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.LocalWindowExceptionHandlerFactory
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowExceptionHandler
import androidx.compose.ui.window.WindowExceptionHandlerFactory
import androidx.compose.ui.window.WindowState
import io.sentry.Sentry
import mu.KotlinLogging
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplicationScope.BaseMontaWindow(
    title: String,
    state: WindowState,
    windowGainedFocus: () -> Unit = {},
    windowLostFocus: () -> Unit = {},
    block: @Composable FrameWindowScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalWindowExceptionHandlerFactory.provides(
            WindowExceptionHandlerFactory { window ->
                WindowExceptionHandler { throwable ->
                    Sentry.captureException(throwable)
                    logger.error("Window Exception", throwable)
                    window.dispatchEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                    throw throwable
                }
            }
        )
    ) {
        Window(
            title = title,
            state = state,
            focusable = true,
            onCloseRequest = {
                this.exitApplication()
            }
        ) {
            DisposableEffect(Unit) {
                window.addWindowFocusListener(object : WindowFocusListener {
                    override fun windowGainedFocus(
                        e: WindowEvent
                    ) {
                        windowGainedFocus()
                    }

                    override fun windowLostFocus(
                        e: WindowEvent
                    ) {
                        windowLostFocus()
                    }
                })
                onDispose {}
            }
            block()
        }
    }
}
