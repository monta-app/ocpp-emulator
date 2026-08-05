package com.monta.ocpp.emulator.chargepoint.core.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.core.repository.ChargePointRepository
import com.monta.ocpp.emulator.designsystem.ui.component.InputField
import com.monta.ocpp.emulator.designsystem.ui.component.PrimaryIconButton
import com.monta.ocpp.emulator.designsystem.ui.component.TextTooltip
import com.monta.ocpp.emulator.designsystem.ui.component.mutedForegroundColor
import com.monta.ocpp.emulator.navigation.model.Screen
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.navigation.ui.PageScaffold
import com.monta.ocpp.emulator.platform.database.extension.idValue
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChargePointsScreen() {
    var searchQuery by remember {
        mutableStateOf("")
    }

    PageScaffold(
        title = "Charge Points",
    ) {
        Column {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SearchTextField(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { newQuery ->
                        searchQuery = newQuery
                    },
                )
                AddChargePointButton()
            }
            ChargePointsListView(searchQuery)
        }
    }
}

@Composable
private fun RowScope.SearchTextField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    InputField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier.weight(1F),
        placeholder = "Search charge points…",
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.size(16.dp),
                tint = mutedForegroundColor(),
            )
        },
        trailingIcon = if (searchQuery.isNotEmpty()) {
            {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            onSearchQueryChange("")
                        },
                    tint = mutedForegroundColor(),
                )
            }
        } else {
            null
        },
    )
}

@Composable
private fun ChargePointsListView(
    searchQuery: String,
) {
    val coroutineScope = rememberCoroutineScope()

    val navigator: Navigator by injectAnywhere()
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

    ChargePointTable(
        chargePoints = filteredChargePoints,
        onRowClick = { chargePoint ->
            navigator.navigate(
                Screen.ChargePoint(
                    chargePointId = chargePoint.idValue,
                ),
            )
        },
    )
}

// Helper extension function to filter charge points based on the search query
private fun ChargePointDAO.matchesSearchQuery(
    query: String,
): Boolean {
    // Adjust the logic here based on your ChargePointDAO structure
    if (query.isEmpty()) {
        return true
    }

    return name.contains(query, ignoreCase = true) || identity.contains(query, ignoreCase = true)
}

@Composable
private fun AddChargePointButton() {
    val navigator: Navigator by injectAnywhere()

    TextTooltip("Add a new charge point") {
        PrimaryIconButton(
            onClick = {
                navigator.navigate(
                    Screen.CreateChargePoint(),
                )
            },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add charge point",
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
