package com.monta.ocpp.emulator.interceptor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.designsystem.ui.component.MontaIcon
import com.monta.ocpp.emulator.navigation.model.Screen
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import kotlinx.coroutines.launch

/** Top-level destinations shown in the [BasePage] bottom navigation bar. */
enum class BottomNavDestination {
    ChargePoints,
    ChargePoint,
    Vehicles,
}

@Composable
fun BasePage(
    // Which bottom-navigation destination this page represents; drives the selected item.
    selectedDestination: BottomNavDestination? = null,
    // State
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    // Drawer
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawer: @Composable (ColumnScope.() -> Unit) = {},
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val navigator: Navigator by injectAnywhere()
    val chargePointRepository: ChargePointRepository by injectAnywhere()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        scaffoldState = scaffoldState,
        topBar = {
            topBar()
        },
        drawerGesturesEnabled = true,
        drawerElevation = 0.dp,
        drawerContent = drawer,
        drawerShape = drawerShape,
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = selectedDestination == BottomNavDestination.ChargePoints,
                    onClick = {
                        navigator.navigateTopLevel(Screen.ChargePoints)
                    },
                    icon = {
                        MontaIcon(
                            iconName = "ev-charger",
                            contentDescription = "Charge Points",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = {
                        Text("Charge Points")
                    },
                )
                BottomNavigationItem(
                    selected = selectedDestination == BottomNavDestination.ChargePoint,
                    onClick = {
                        val connectedChargePoints = chargePointRepository.getConnectedChargePoints().map { it.idValue }

                        val lastActive =
                            navigator.currentChargePointId ?: connectedChargePoints.firstOrNull()

                        if (connectedChargePoints.isEmpty()) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "No connected charge points",
                                    actionLabel = "OK",
                                )
                            }
                        } else {
                            if (lastActive in connectedChargePoints && lastActive != null) {
                                navigator.navigateTopLevel(
                                    Screen.ChargePoint(
                                        chargePointId = lastActive,
                                    ),
                                )
                            } else {
                                navigator.navigateTopLevel(
                                    Screen.ChargePoint(
                                        chargePointId = connectedChargePoints.first(),
                                    ),
                                )
                            }
                        }
                    },
                    icon = {
                        MontaIcon(
                            iconName = "globe-check",
                            contentDescription = "Connected Charge Points",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = {
                        Text("Connected")
                    },
                )
                BottomNavigationItem(
                    selected = selectedDestination == BottomNavDestination.Vehicles,
                    onClick = {
                        navigator.navigateTopLevel(Screen.Vehicles)
                    },
                    icon = {
                        MontaIcon(
                            iconName = "car",
                            contentDescription = "Vehicles",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = {
                        Text("Vehicles")
                    },
                )
            }
        },
        floatingActionButton = floatingActionButton,
    ) {
        Row {
            Box(
                modifier = Modifier.weight(1f)
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding(),
                    ),
            ) {
                content()
            }
        }
    }
}

class NavShape(
    private val widthOffset: Dp,
    private val scale: Float,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        return Outline.Rectangle(
            Rect(
                Offset.Zero,
                Offset(
                    size.width * scale + with(density) { widthOffset.toPx() },
                    size.height,
                ),
            ),
        )
    }
}
