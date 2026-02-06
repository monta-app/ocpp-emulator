package com.monta.ocpp.emulator.common.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
    .withLocale(Locale.US)
    .withZone(ZoneId.systemDefault())

fun Instant.toReadable(): String {
    return formatter.format(this)
}

@Composable
fun getButtonStateColor(
    isActive: Boolean,
): Color {
    return if (isActive) {
        MaterialTheme.colors.primary
    } else {
        Color.LightGray
    }
}

@Composable
fun Double.wattToKilowattString(): String {
    return (this / 1000.0).toKilowattString()
}

@Composable
fun Double.toAmpString(): String {
    return String.format("%.1f", this)
}

@Composable
fun Double.toKilowattString(): String {
    return String.format("%.2f", this)
}

fun String.toColor(): Color {
    val cleanedString = this.replace("#", "")
    val r = cleanedString.substring(0, 2).toInt(16)
    val g = cleanedString.substring(2, 4).toInt(16)
    val b = cleanedString.substring(4, 6).toInt(16)
    return Color(r, g, b)
}
