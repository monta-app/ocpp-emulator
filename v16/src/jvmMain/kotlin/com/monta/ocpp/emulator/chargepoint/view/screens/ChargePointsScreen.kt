package com.monta.ocpp.emulator.chargepoint.view.screens

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.repository.ChargePointRepository
import com.monta.ocpp.emulator.chargepoint.view.components.ChargePointCard
import com.monta.ocpp.emulator.common.components.TextTooltip
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.view.NavigationViewModel
import com.monta.ocpp.emulator.interceptor.view.BasePage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChargePointsScreen() {
    BasePage(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Charge Points")
                }
            )
        },
        floatingActionButton = {
            AddChargePointFab()
        }
    ) {
        ChargePointsListView()
    }
}

@Composable
private fun ChargePointsListView() {
    val coroutineScope = rememberCoroutineScope()

    val screenViewModel: NavigationViewModel by injectAnywhere()
    val chargePointRepository: ChargePointRepository by injectAnywhere()

    val chargePoints by produceState(listOf<ChargePointDAO>()) {
        chargePointRepository.getAllFlow(coroutineScope)
            .collectLatest { newList ->
                value = newList
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 384.dp)
    ) {
        items(chargePoints) { chargePoint ->
            ChargePointCard(
                chargePoint = chargePoint
            ) {
                screenViewModel.navigateTo(
                    NavigationViewModel.Screen.ChargePoint(
                        chargePointId = chargePoint.idValue
                    )
                )
            }
        }
    }
}

@Composable
private fun AddChargePointFab() {
    val screenViewModel: NavigationViewModel by injectAnywhere()

    TextTooltip("Add a new charge point") {
        FloatingActionButton(
            onClick = {
                screenViewModel.navigateTo(
                    NavigationViewModel.Screen.CreateChargePoint()
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add charge point"
            )
        }
    }
}
