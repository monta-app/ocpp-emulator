package com.monta.ocpp.emulator.chargepoint.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
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
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.view.components.chargePointComponent
import com.monta.ocpp.emulator.chargepoint.view.components.chargePointLogComponent
import com.monta.ocpp.emulator.chargepoint.view.components.pbm.PbmDialog
import com.monta.ocpp.emulator.chargepoint.view.components.pbm.pbmButtons
import com.monta.ocpp.emulator.chargepointconnector.view.ConnectorList
import com.monta.ocpp.emulator.common.components.BackButton
import com.monta.ocpp.emulator.common.components.DualColumView
import com.monta.ocpp.emulator.common.components.InterceptionToggle
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.view.NavigationViewModel
import com.monta.ocpp.emulator.interceptor.view.BasePage
import com.monta.ocpp.emulator.interceptor.view.InterceptorConfigComponent
import com.monta.ocpp.emulator.interceptor.view.NavShape
import com.monta.ocpp.emulator.v16.connection.ConnectionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ChargePointPage(
    chargePointId: Long
) {
    val chargePointRepository: ChargePointRepository by injectAnywhere()

    val coroutineScope = rememberCoroutineScope()
    val connectionManager: ConnectionManager by injectAnywhere()

    var chargePoint: ChargePointDAO? by remember { mutableStateOf(null) }

    LaunchedEffect(chargePointId) {
        coroutineScope.launch {
            chargePointRepository.getByIdFlow(
                coroutineScope = coroutineScope,
                id = chargePointId
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
            chargePoints.filter { check -> check.connected || check.idValue == chargePointId }
        )
    }
}

@Composable
private fun innerChargePointPage(
    chargePoint: ChargePointDAO,
    connectedChargePoints: List<ChargePointDAO>
) {
    val navigationViewModel: NavigationViewModel by injectAnywhere()
    val coroutineScope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState(
        drawerState = DrawerState(DrawerValue.Closed)
    )

    var selectedTab by remember {
        mutableStateOf(
            connectedChargePoints.indexOfFirst { connectedChargePoint ->
                connectedChargePoint.idValue == chargePoint.idValue
            }
        )
    }

    if (selectedTab == -1) {
        selectedTab = 0
    }

    BasePage(
        scaffoldState = scaffoldState,
        drawerShape = NavShape(
            widthOffset = 320.dp,
            scale = 0f
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
                        navigationViewModel.chargePointsScreen()
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
                        }
                    )
                }
            )
        },
        drawer = {
            InterceptorConfigComponent(chargePoint.idValue)
        }
    ) {
        Column {
            ScrollableTabRow(
                selectedTabIndex = selectedTab
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
                        onClick = {}
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
                        onClick = {
                            selectedTab = idx
                            navigationViewModel.navigateTo(
                                NavigationViewModel.Screen.ChargePoint(
                                    chargePointId = chargePoint.idValue
                                )
                            )
                        }
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
                    chargePointLogComponent(chargePoint.idValue)
                }
            )
            // Shows a dialog for the user to interact with PBM features
            PbmDialog(chargePoint)
        }
    }
}
