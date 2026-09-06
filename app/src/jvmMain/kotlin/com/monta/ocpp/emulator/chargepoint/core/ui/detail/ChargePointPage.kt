package com.monta.ocpp.emulator.chargepoint.core.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
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
import com.monta.ocpp.emulator.designsystem.ui.component.DualColumView
import com.monta.ocpp.emulator.designsystem.ui.component.InterceptionToggle
import com.monta.ocpp.emulator.designsystem.ui.component.TabBar
import com.monta.ocpp.emulator.interceptor.ui.InterceptorConfigComponent
import com.monta.ocpp.emulator.navigation.model.Screen
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.navigation.ui.NavShape
import com.monta.ocpp.emulator.navigation.ui.PageScaffold
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

    PageScaffold(
        title = "Charge Point — ${chargePoint.identity}",
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
        scaffoldState = scaffoldState,
        drawerShape = NavShape(
            widthOffset = 320.dp,
            scale = 0f,
        ),
        drawer = {
            InterceptorConfigComponent(chargePoint.idValue)
        },
    ) {
        Column {
            // One tab per connected charge point, with the selection derived from
            // the route so switching tabs is purely a navigation call. A lone tab
            // has nothing to switch to (and a one-pill strip reads as a weird ring
            // around the label), so the bar only shows once there's a choice.
            if (connectedChargePoints.size > 1) {
                TabBar(
                    tabs = connectedChargePoints,
                    selected = connectedChargePoints.firstOrNull { tab ->
                        tab.idValue == chargePoint.idValue
                    } ?: connectedChargePoints.first(),
                    label = { tab -> tab.name.ifBlank { tab.identity } },
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 8.dp,
                        end = 8.dp,
                    ),
                    onSelect = { tab ->
                        navigator.switchChargePoint(
                            Screen.ChargePoint(
                                chargePointId = tab.idValue,
                            ),
                        )
                    },
                )
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
