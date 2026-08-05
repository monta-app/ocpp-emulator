package com.monta.ocpp.emulator.navigation.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.monta.ocpp.emulator.navigation.model.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Singleton

/**
 * App-wide navigation entry point.
 *
 * The actual back stack lives in the `NavHost` inside `MainWindow`; this singleton exists so that
 * navigation can be triggered from anywhere the codebase already reaches for a dependency via
 * `injectAnywhere()` — including plain (non-composable) functions and the separate interceptor
 * windows that live outside the `NavHost` composition. Navigation intents are pushed onto
 * [commands], which `MainWindow` collects and applies to the real `NavHostController`.
 *
 * It also holds two pieces of window-scoped state that must be readable from outside the NavHost:
 * [windowHasFocus] (used to gate log auto-scroll) and [currentChargePointId] (the charge point
 * currently on screen, needed by `SendMessageWindow`).
 */
@Singleton
class Navigator {

    var windowHasFocus: Boolean by mutableStateOf(true)

    /**
     * Id of the charge-point detail currently shown. Kept up to date by `MainWindow` as the back
     * stack changes and retained afterwards as the "last active" charge point, mirroring the old
     * `lastActiveChargePointId` behaviour.
     */
    var currentChargePointId: Long? by mutableStateOf(null)

    private val _commands = Channel<NavCommand>(Channel.BUFFERED)
    val commands = _commands.receiveAsFlow()

    /** Push a destination onto the back stack. */
    fun navigate(
        route: Screen,
    ) {
        send(NavCommand.Navigate(route))
    }

    /**
     * Navigate to a top-level destination (the bottom navigation bar), collapsing the back stack
     * to the graph's start destination so top-level switches don't pile up history.
     */
    fun navigateTopLevel(
        route: Screen,
    ) {
        send(NavCommand.NavigateTopLevel(route))
    }

    /**
     * Switch the charge-point detail in place (the connected-charge-point tabs), replacing the
     * current detail entry instead of stacking a new one so "back" still returns to the list.
     */
    fun switchChargePoint(
        route: Screen.ChargePoint,
    ) {
        send(NavCommand.SwitchChargePoint(route))
    }

    /** Pop the current destination off the back stack. */
    fun back() {
        send(NavCommand.Back)
    }

    /** The charge point currently on screen, or throws if none — replaces the old unchecked cast. */
    fun requireChargePointId(): Long {
        return requireNotNull(currentChargePointId) {
            "No charge point is currently active"
        }
    }

    private fun send(
        command: NavCommand,
    ) {
        _commands.trySend(command)
    }

    sealed interface NavCommand {
        data class Navigate(val route: Screen) : NavCommand
        data class NavigateTopLevel(val route: Screen) : NavCommand
        data class SwitchChargePoint(val route: Screen.ChargePoint) : NavCommand
        data object Back : NavCommand
    }
}
