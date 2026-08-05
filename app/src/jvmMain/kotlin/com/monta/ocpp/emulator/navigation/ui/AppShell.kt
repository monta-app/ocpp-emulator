package com.monta.ocpp.emulator.navigation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.designsystem.ui.component.cardBorderColor
import com.monta.ocpp.emulator.navigation.model.TopLevelDestination
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.platform.util.injectAnywhere

/**
 * The window-level layout: [AppSidebar] on the left, [content] (the `NavHost`) inside a rounded,
 * hairline-bordered inset card — the shadcn "inset sidebar" look. The sidebar sits directly on the
 * window background and collapses off-canvas via [Navigator.sidebarVisible] (toggled from
 * [PageScaffold]'s header trigger).
 */
@Composable
fun AppShell(
    selectedDestination: TopLevelDestination?,
    content: @Composable () -> Unit,
) {
    val navigator: Navigator by injectAnywhere()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        AnimatedVisibility(
            visible = navigator.sidebarVisible,
            enter = expandHorizontally(),
            exit = shrinkHorizontally(),
        ) {
            AppSidebar(selectedDestination)
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(
                    start = if (navigator.sidebarVisible) 0.dp else 8.dp,
                    top = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp,
                ),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            border = BorderStroke(1.dp, cardBorderColor()),
            elevation = 0.dp,
        ) {
            content()
        }
    }
}
