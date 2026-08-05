package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Visual style of a [Badge], modelled after shadcn/ui's badge variants.
 *
 * The first four map to shadcn's `default`/`secondary`/`destructive`/`outline`;
 * [Success], [Warning] and [Neutral] are added for status semantics
 * (online/offline, healthy/degraded, inactive, …).
 */
enum class BadgeVariant {
    Default,
    Secondary,
    Success,
    Warning,
    Destructive,
    Neutral,
    Outline,
}

// Semantic colors not present in the Material palette. Kept saturated so white /
// dark foreground text stays legible on both the light and dark app themes.
private val SuccessColor = Color(0xFF2E9E44)
private val WarningColor = Color(0xFFF9A825)

private data class BadgeColors(
    val background: Color,
    val content: Color,
    val border: Color,
)

@Composable
private fun BadgeVariant.colors(): BadgeColors {
    val palette = MaterialTheme.colors
    return when (this) {
        BadgeVariant.Default -> BadgeColors(palette.primary, palette.onPrimary, Color.Transparent)
        BadgeVariant.Secondary -> BadgeColors(palette.secondary, palette.onSecondary, Color.Transparent)
        BadgeVariant.Success -> BadgeColors(SuccessColor, Color.White, Color.Transparent)
        BadgeVariant.Warning -> BadgeColors(WarningColor, Color.Black, Color.Transparent)
        BadgeVariant.Destructive -> BadgeColors(palette.error, palette.onError, Color.Transparent)
        BadgeVariant.Neutral -> BadgeColors(
            background = palette.onSurface.copy(alpha = 0.12f),
            content = palette.onSurface.copy(alpha = 0.7f),
            border = Color.Transparent,
        )
        BadgeVariant.Outline -> BadgeColors(
            background = Color.Transparent,
            content = palette.onSurface.copy(alpha = 0.8f),
            border = palette.onSurface.copy(alpha = 0.2f),
        )
    }
}

/**
 * Small pill-shaped label for compact status such as online/offline, mode or
 * protocol version. Similar in look and feel to shadcn/ui's `<Badge>`.
 *
 * [leadingIcon] is an optional slot rendered before the text; it inherits the
 * badge's content color via [LocalContentColor], so a plain [MontaIcon] or
 * [Icon] tints itself automatically. Prefer the [iconName] / [imageVector]
 * overloads for the common cases.
 */
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val badgeColors = variant.colors()
    val shape = RoundedCornerShape(percent = 50)

    Row(
        modifier = modifier
            .clip(shape)
            .background(badgeColors.background)
            .border(
                width = 1.dp,
                color = badgeColors.border,
                shape = shape,
            )
            .padding(
                horizontal = 10.dp,
                vertical = 3.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides badgeColors.content) {
            leadingIcon?.invoke()
            Text(
                text = text,
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.SemiBold,
                color = badgeColors.content,
                maxLines = 1,
            )
        }
    }
}

/**
 * [Badge] with an embedded SVG icon loaded from `icons/[iconName].svg` on the
 * classpath (same convention as [MontaIcon]).
 */
@Composable
fun Badge(
    text: String,
    iconName: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
) {
    Badge(
        text = text,
        modifier = modifier,
        variant = variant,
        leadingIcon = {
            Icon(
                painter = svgPainterResource("icons/$iconName.svg"),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = LocalContentColor.current,
            )
        },
    )
}

/**
 * [Badge] with an embedded Material [ImageVector] icon.
 */
@Composable
fun Badge(
    text: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
) {
    Badge(
        text = text,
        modifier = modifier,
        variant = variant,
        leadingIcon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = LocalContentColor.current,
            )
        },
    )
}
