package com.monta.ocpp.emulator

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.monta.ocpp.emulator.chargepoint.core.ui.detail.ChargePointPage
import com.monta.ocpp.emulator.chargepoint.core.ui.form.CreateChargePointDialog
import com.monta.ocpp.emulator.chargepoint.core.ui.list.ChargePointsScreen
import com.monta.ocpp.emulator.designsystem.ui.component.BaseMontaWindow
import com.monta.ocpp.emulator.designsystem.ui.theme.AppThemeViewModel
import com.monta.ocpp.emulator.designsystem.ui.theme.setupAppThemeMenu
import com.monta.ocpp.emulator.navigation.model.Screen
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.platform.update.ui.UpdateDialog
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.vehicle.ui.VehicleScreen

@Preview
@Composable
fun ApplicationScope.MainWindow() {
    val appThemeViewModel: AppThemeViewModel by injectAnywhere()
    val navigator: Navigator by injectAnywhere()

    val windowState = rememberWindowState(
        size = DpSize(1200.dp, 1400.dp),
        position = WindowPosition.Aligned(
            Alignment.Center,
        ),
    )

    BaseMontaWindow(
        title = "OCPP Emulator V16",
        state = windowState,
        windowGainedFocus = {
            navigator.windowHasFocus = true
        },
        windowLostFocus = {
            navigator.windowHasFocus = false
        },
    ) {
        setupAppThemeMenu(appThemeViewModel)

        MaterialTheme(
            colors = appThemeViewModel.getColors(),
        ) {
            val navController = rememberNavController()

            // Apply navigation intents emitted from anywhere in the app onto the real back stack.
            LaunchedEffect(navController) {
                navigator.commands.collect { command ->
                    when (command) {
                        is Navigator.NavCommand.Navigate -> {
                            navController.navigate(command.route)
                        }

                        is Navigator.NavCommand.NavigateTopLevel -> {
                            navController.navigate(command.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }

                        is Navigator.NavCommand.SwitchChargePoint -> {
                            navController.navigate(command.route) {
                                popUpTo<Screen.ChargePoints> {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }

                        Navigator.NavCommand.Back -> {
                            navController.popBackStack()
                        }
                    }
                }
            }

            // Track the on-screen charge point so windows outside the NavHost (e.g. SendMessageWindow)
            // can resolve it, and so the bottom bar can restore the last active charge point.
            LaunchedEffect(navController) {
                navController.currentBackStackEntryFlow.collect { entry ->
                    runCatching { entry.toRoute<Screen.ChargePoint>() }
                        .getOrNull()
                        ?.let { route ->
                            navigator.currentChargePointId = route.chargePointId
                        }
                }
            }

            NavHost(
                navController = navController,
                startDestination = Screen.ChargePoints,
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colors.background),
                // Swap pages instantly, matching the previous when-based navigation. The default
                // cross-fade briefly revealed the window background as a white flash on switch.
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
            ) {
                composable<Screen.ChargePoints> {
                    ChargePointsScreen()
                }
                composable<Screen.Vehicles> {
                    VehicleScreen()
                }
                composable<Screen.ChargePoint> { backStackEntry ->
                    ChargePointPage(backStackEntry.toRoute<Screen.ChargePoint>().chargePointId)
                }
                dialog<Screen.CreateChargePoint> { backStackEntry ->
                    CreateChargePointDialog(backStackEntry.toRoute<Screen.CreateChargePoint>().chargePointId)
                }
            }
            // Shows a dialog notifying users an update is available if there is one
            UpdateDialog()
        }
    }
}
