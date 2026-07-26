package com.monta.ocpp.emulator.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.decodeToSvgPainter

/**
 * Loads an SVG [Painter] from the application classpath, replacing the
 * deprecated `androidx.compose.ui.res.painterResource(String)`.
 */
@Composable
fun svgPainterResource(
    resourcePath: String,
): Painter {
    val density = LocalDensity.current
    return remember(resourcePath, density) {
        SvgResources.readBytes(resourcePath).decodeToSvgPainter(density)
    }
}

private object SvgResources {
    fun readBytes(
        resourcePath: String,
    ): ByteArray {
        val stream = requireNotNull(javaClass.classLoader.getResourceAsStream(resourcePath)) {
            "Resource not found on classpath: $resourcePath"
        }
        return stream.use { it.readBytes() }
    }
}
