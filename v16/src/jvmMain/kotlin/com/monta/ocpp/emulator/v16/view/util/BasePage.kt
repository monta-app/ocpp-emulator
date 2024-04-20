package com.monta.ocpp.emulator.v16.view.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomAppBar
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
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
import com.monta.ocpp.emulator.common.components.MontaIcon
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.v16.data.repository.ChargePointRepository
import com.monta.ocpp.emulator.v16.data.util.idValue
import com.monta.ocpp.emulator.v16.view.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun BasePage(
    // State
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    // Drawer
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawer: @Composable (ColumnScope.() -> Unit) = {},
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val navigationViewModel: NavigationViewModel by injectAnywhere()
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
            BottomAppBar {
                IconButton(
                    onClick = {
                        navigationViewModel.chargePointsScreen()
                    }
                ) {
                    MontaIcon(
                        iconName = "database",
                        contentDescription = "Charge Points",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = {
                        val connectedChargePoints = chargePointRepository.getConnectedChargePoints().map { it.idValue }

                        val lastActive =
                            navigationViewModel.lastActiveChargePointId ?: connectedChargePoints.firstOrNull()

                        if (connectedChargePoints.isEmpty()) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "No connected charge points",
                                    actionLabel = "OK"
                                )
                            }
                        } else {
                            if (lastActive in connectedChargePoints && lastActive != null) {
                                navigationViewModel.currentScreen = NavigationViewModel.Screen.ChargePoint(
                                    chargePointId = lastActive
                                )
                            } else {
                                navigationViewModel.currentScreen = NavigationViewModel.Screen.ChargePoint(
                                    chargePointId = connectedChargePoints.first()
                                )
                            }
                        }
                    }
                ) {
                    MontaIcon(
                        iconName = "ev_charger",
                        contentDescription = "Connected Charge Points",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = {
                        navigationViewModel.currentScreen = NavigationViewModel.Screen.Vehicles
                    }
                ) {
                    MontaIcon(
                        iconName = "ev_car",
                        contentDescription = "Vehicles",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        floatingActionButton = floatingActionButton
    ) {
        Row {
            Box(
                modifier = Modifier.weight(1f)
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding()
                    )
            ) {
                content()
            }
        }
    }
}

class NavShape(
    private val widthOffset: Dp,
    private val scale: Float
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                Offset.Zero,
                Offset(
                    size.width * scale + with(density) { widthOffset.toPx() },
                    size.height
                )
            )
        )
    }
}
