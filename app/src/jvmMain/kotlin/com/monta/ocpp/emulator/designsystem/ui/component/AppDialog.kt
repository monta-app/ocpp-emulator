package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * The shared shadcn `<Dialog>` container — a bordered, rounded surface floating
 * above a scrim with a pronounced shadow. Kept separate from [AppDialog] so
 * dialogs that manage their own window (e.g. a Navigation Compose `dialog`
 * destination) can reuse the exact same look without nesting a second [Dialog].
 */
@Composable
fun DialogSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // Material tints elevated surfaces lighter in dark themes to fake
    // elevation; that overlay is what washes the near-black surface out to
    // grey. Disable it so the dialog keeps its true colour — the border and the
    // scrim (plus the drop shadow) do the separating instead.
    CompositionLocalProvider(LocalElevationOverlay provides null) {
        Surface(
            modifier = Modifier.widthIn(min = 360.dp, max = 640.dp)
                .then(modifier),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            border = BorderStroke(1.dp, cardBorderColor()),
            elevation = 24.dp,
            content = content,
        )
    }
}

/**
 * shadcn-style modal dialog: a [DialogSurface] with a header (bold [title] plus
 * an optional muted [description]), an optional [content] body, and a
 * right-aligned footer built from [dismissButton] / [confirmButton] — the
 * primary action sits on the right, matching shadcn.
 *
 * Built on the plain [Dialog] rather than Material's `AlertDialog`, which sizes
 * its slots with intrinsic measurements that scrollable content cannot provide.
 */
@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    dismissButton: (@Composable () -> Unit)? = null,
    confirmButton: (@Composable () -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        DialogSurface(
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (description != null) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.body2,
                            color = mutedForegroundColor(),
                        )
                    }
                }
                content?.invoke(this)
                if (confirmButton != null || dismissButton != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.End,
                        ),
                    ) {
                        dismissButton?.invoke()
                        confirmButton?.invoke()
                    }
                }
            }
        }
    }
}
