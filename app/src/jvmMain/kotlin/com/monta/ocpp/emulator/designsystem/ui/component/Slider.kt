package com.monta.ocpp.emulator.designsystem.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private val TrackHeight = 6.dp
private val ThumbSize = 16.dp

/**
 * shadcn-style slider: thin rounded track, primary active portion, and a small
 * circular surface thumb with a primary ring — no Material ripple halo. Drag or
 * click anywhere on the track to set the value. Replaces
 * [androidx.compose.material.Slider], whose thumb/track metrics aren't
 * customisable.
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    // Keep the latest callbacks visible inside pointerInput, which outlives
    // recompositions.
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)

    val fraction = if (valueRange.endInclusive > valueRange.start) {
        ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    } else {
        0f
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.CenterStart,
    ) {
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat()
        val thumbSizePx = with(density) { ThumbSize.toPx() }

        fun valueAt(
            x: Float,
        ): Float {
            val tapFraction = (x / widthPx).coerceIn(0f, 1f)
            return valueRange.start + tapFraction * (valueRange.endInclusive - valueRange.start)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .clip(RoundedCornerShape(TrackHeight / 2))
                .background(cardBorderColor()),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .height(TrackHeight)
                .clip(RoundedCornerShape(TrackHeight / 2))
                .background(MaterialTheme.colors.primary),
        )
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = ((widthPx - thumbSizePx) * fraction).roundToInt(),
                        y = 0,
                    )
                }
                .size(ThumbSize)
                .clip(CircleShape)
                .background(MaterialTheme.colors.surface)
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape,
                ),
        )
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(valueRange, widthPx) {
                        detectTapGestures(
                            onTap = { offset ->
                                currentOnValueChange(valueAt(offset.x))
                                currentOnValueChangeFinished?.invoke()
                            },
                        )
                    }
                    .pointerInput(valueRange, widthPx) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentOnValueChange(valueAt(offset.x))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentOnValueChange(valueAt(change.position.x))
                            },
                            onDragEnd = {
                                currentOnValueChangeFinished?.invoke()
                            },
                            onDragCancel = {
                                currentOnValueChangeFinished?.invoke()
                            },
                        )
                    },
            )
        }
    }
}
