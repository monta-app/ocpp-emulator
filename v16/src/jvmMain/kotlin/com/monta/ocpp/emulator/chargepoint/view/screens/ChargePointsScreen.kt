package com.monta.ocpp.emulator.chargepoint.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    var searchQuery by remember {
        mutableStateOf("")
    }

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
        Column {
            SearchTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                searchQuery = searchQuery,
                onSearchQueryChange = { newQuery ->
                    searchQuery = newQuery
                }
            )
            ChargePointsListView(searchQuery)
        }
    }
}

@Composable
private fun SearchTextField(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    // Search Bar
    TextField(
        value = searchQuery,
        onValueChange = {
            onSearchQueryChange(it)
        },
        modifier = modifier
            .fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchQueryChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Search"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun ChargePointsListView(
    searchQuery: String
) {
    val coroutineScope = rememberCoroutineScope()

    val screenViewModel: NavigationViewModel by injectAnywhere()
    val chargePointRepository: ChargePointRepository by injectAnywhere()

    val chargePoints by produceState(initialValue = listOf<ChargePointDAO>()) {
        chargePointRepository.getAllFlow(coroutineScope)
            .collectLatest { newList ->
                value = newList
            }
    }

    // Filter the list based on the search query
    val filteredChargePoints = chargePoints.filter { chargePoint ->
        chargePoint.matchesSearchQuery(searchQuery)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 384.dp)
    ) {
        items(filteredChargePoints) { chargePoint ->
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

// Helper extension function to filter charge points based on the search query
private fun ChargePointDAO.matchesSearchQuery(query: String): Boolean {
    // Adjust the logic here based on your ChargePointDAO structure
    if (query.isEmpty()) {
        return true
    }

    return name.contains(query, ignoreCase = true) || identity.contains(query, ignoreCase = true)
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
