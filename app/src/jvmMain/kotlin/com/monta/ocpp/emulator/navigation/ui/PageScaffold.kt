package com.monta.ocpp.emulator.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.designsystem.ui.component.CardDivider
import com.monta.ocpp.emulator.designsystem.ui.component.cardBorderColor
import com.monta.ocpp.emulator.designsystem.ui.component.mutedForegroundColor
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.platform.util.injectAnywhere

/**
 * Per-screen scaffold for pages hosted inside [AppShell]'s inset card: a flat header (sidebar
 * trigger, hairline separator, title, trailing [actions]) over the page content, with an optional
 * [drawer] (used by the charge-point detail for the interceptor config panel). No bottom bar —
 * top-level navigation lives in the persistent [AppSidebar].
 */
@Composable
fun PageScaffold(
    title: String,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawer: (@Composable ColumnScope.() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        // Transparent so the AppShell inset card's surface colour shows through.
        backgroundColor = Color.Transparent,
        topBar = {
            PageHeader(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
            )
        },
        drawerContent = drawer,
        drawerShape = drawerShape,
        drawerElevation = 0.dp,
        drawerGesturesEnabled = drawer != null,
        floatingActionButton = floatingActionButton,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            content()
        }
    }
}

@Composable
private fun PageHeader(
    title: String,
    navigationIcon: (@Composable () -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
) {
    val navigator: Navigator by injectAnywhere()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    navigator.sidebarVisible = !navigator.sidebarVisible
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle sidebar",
                    modifier = Modifier.size(18.dp),
                    tint = mutedForegroundColor(),
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(cardBorderColor()),
            )
            navigationIcon?.invoke()
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Medium,
            )
            Spacer(
                modifier = Modifier.weight(1f),
            )
            actions()
        }
        CardDivider()
    }
}

/**
 * Drawer shape that limits the drawer's tap-to-dismiss scrim to the drawer itself. Moved here from
 * the old bottom-nav `BasePage`.
 */
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
