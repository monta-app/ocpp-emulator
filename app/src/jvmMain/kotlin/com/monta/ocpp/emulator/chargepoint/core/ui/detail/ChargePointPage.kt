package com.monta.ocpp.emulator.chargepoint.core.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.connector.ui.ConnectorList
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.core.ui.detail.chargePointComponent
import com.monta.ocpp.emulator.chargepoint.core.ui.detail.chargePointLogComponent
import com.monta.ocpp.emulator.chargepoint.core.ui.pbm.PbmDialog
import com.monta.ocpp.emulator.chargepoint.core.ui.pbm.pbmButtons
import com.monta.ocpp.emulator.designsystem.ui.component.BackButton
import com.monta.ocpp.emulator.designsystem.ui.component.CardDivider
import com.monta.ocpp.emulator.designsystem.ui.component.DualColumView
import com.monta.ocpp.emulator.designsystem.ui.component.InterceptionToggle
import com.monta.ocpp.emulator.designsystem.ui.component.mutedForegroundColor
import com.monta.ocpp.emulator.interceptor.ui.BasePage
import com.monta.ocpp.emulator.interceptor.ui.BottomNavDestination
import com.monta.ocpp.emulator.interceptor.ui.InterceptorConfigComponent
import com.monta.ocpp.emulator.interceptor.ui.NavShape
import com.monta.ocpp.emulator.navigation.model.Screen
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.ocpp.v16.connection.ConnectionManager
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ChargePointPage(
    chargePointId: Long,
) {
    val chargePointRepository: ChargePointRepository by injectAnywhere()

    val coroutineScope = rememberCoroutineScope()
    val connectionManager: ConnectionManager by injectAnywhere()

    var chargePoint: ChargePointDAO? by remember { mutableStateOf(null) }

    LaunchedEffect(chargePointId) {
        coroutineScope.launch {
            chargePointRepository.getByIdFlow(
                coroutineScope = coroutineScope,
                id = chargePointId,
            ).collectLatest {
                chargePoint = it
            }
        }
        connectionManager.connect(chargePointId)
    }

    val chargePoints by produceState(listOf<ChargePointDAO>()) {
        chargePointRepository.getAllFlow(coroutineScope)
            .collectLatest { newList ->
                value = newList
            }
    }
    chargePoint?.let {
        innerChargePointPage(
            it,
            chargePoints.filter { check -> check.connected || check.idValue == chargePointId },
        )
    }
}

@Composable
private fun innerChargePointPage(
    chargePoint: ChargePointDAO,
    connectedChargePoints: List<ChargePointDAO>,
) {
    val navigator: Navigator by injectAnywhere()
    val coroutineScope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState(
        drawerState = DrawerState(DrawerValue.Closed),
    )

    var selectedTab by remember {
        mutableStateOf(
            connectedChargePoints.indexOfFirst { connectedChargePoint ->
                connectedChargePoint.idValue == chargePoint.idValue
            },
        )
    }

    if (selectedTab == -1) {
        selectedTab = 0
    }

    BasePage(
        selectedDestination = BottomNavDestination.ChargePoint,
        scaffoldState = scaffoldState,
        drawerShape = NavShape(
            widthOffset = 320.dp,
            scale = 0f,
        ),
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                title = {
                    // this doesn't show up when actions are defined
                    Text(text = "Charge Point — ${chargePoint.identity}")
                },
                navigationIcon = {
                    BackButton {
                        navigator.back()
                    }
                },
                actions = {
                    InterceptionToggle(
                        checked = scaffoldState.drawerState.isOpen,
                        onCheckedChange = {
                            coroutineScope.launch {
                                if (scaffoldState.drawerState.isOpen) {
                                    scaffoldState.drawerState.close()
                                } else {
                                    scaffoldState.drawerState.open()
                                }
                            }
                        },
                    )
                },
            )
        },
        drawer = {
            InterceptorConfigComponent(chargePoint.idValue)
        },
    ) {
        Column {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primary,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(
                            tabPositions[selectedTab.coerceIn(0, tabPositions.lastIndex)],
                        ),
                        height = 2.dp,
                        color = MaterialTheme.colors.primary,
                    )
                },
                divider = {
                    CardDivider()
                },
            ) {
                if (connectedChargePoints.isEmpty()) {
                    Tab(
                        text = {
                            if (chargePoint.name.isBlank()) {
                                Text(chargePoint.identity)
                            } else {
                                Text(chargePoint.name)
                            }
                        },
                        selected = true,
                        selectedContentColor = MaterialTheme.colors.onSurface,
                        unselectedContentColor = mutedForegroundColor(),
                        onClick = {},
                    )
                }
                connectedChargePoints.forEachIndexed { idx, chargePoint ->
                    Tab(
                        text = {
                            if (chargePoint.name.isBlank()) {
                                Text(chargePoint.identity)
                            } else {
                                Text(chargePoint.name)
                            }
                        },
                        selected = idx == selectedTab,
                        selectedContentColor = MaterialTheme.colors.onSurface,
                        unselectedContentColor = mutedForegroundColor(),
                        onClick = {
                            selectedTab = idx
                            navigator.switchChargePoint(
                                Screen.ChargePoint(
                                    chargePointId = chargePoint.idValue,
                                ),
                            )
                        },
                    )
                }
            }
            DualColumView(
                firstColumn = {
                    chargePointComponent(chargePoint)
                    ConnectorList(chargePoint)
                },
                secondColumn = {
                    pbmButtons()
                    chargePointLogComponent(
                        chargePointId = chargePoint.idValue,
                        modifier = Modifier.weight(1F)
                            .fillMaxWidth(),
                    )
                },
            )
            // Shows a dialog for the user to interact with PBM features
            PbmDialog(chargePoint)
        }
    }
}
