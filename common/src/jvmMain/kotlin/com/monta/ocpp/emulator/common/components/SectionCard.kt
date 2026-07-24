package com.monta.ocpp.emulator.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * shadcn/ui-inspired neutral palette derived from the active Material colours, so
 * every surface tracks the light/dark theme automatically. Kept as `@Composable`
 * helpers (not stored [Color]s) so they re-read [MaterialTheme] on theme change.
 */
@Composable
fun mutedForegroundColor(): Color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f)

@Composable
fun cardBorderColor(): Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)

@Composable
fun mutedSurfaceColor(): Color = MaterialTheme.colors.onSurface.copy(alpha = 0.045f)

/**
 * Flat card with a hairline border and rounded corners — the shadcn `<Card>`
 * look. A quieter alternative to the shadowed [androidx.compose.material.Card]
 * for surfaces that should read as calm containers rather than raised material.
 */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        border = BorderStroke(1.dp, cardBorderColor()),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = verticalArrangement,
            content = content,
        )
    }
}

/**
 * Small, medium-weight heading for a group of fields inside a card — shadcn uses
 * `text-sm font-medium` for the same job.
 */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.body2,
        fontWeight = FontWeight.SemiBold,
    )
}

/**
 * A muted label paired with a value on a single justified row — the
 * definition-list pattern shadcn dashboards use for compact metadata. [value] is
 * a slot, so callers can drop in a [Badge], plain [Text], or anything else.
 */
@Composable
fun DetailRow(
    label: String,
    modifier: Modifier = Modifier,
    value: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = mutedForegroundColor(),
        )
        value()
    }
}

/**
 * [DetailRow] whose value is a right-aligned string in the foreground colour.
 */
@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    DetailRow(
        label = label,
        modifier = modifier,
        value = {
            Text(
                text = value,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
            )
        },
    )
}

/**
 * Hairline separator that matches [SectionCard]'s border, for splitting a card
 * into sections.
 */
@Composable
fun CardDivider(
    modifier: Modifier = Modifier,
) {
    Divider(
        modifier = modifier,
        color = cardBorderColor(),
    )
}
