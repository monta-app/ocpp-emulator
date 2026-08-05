package com.monta.ocpp.emulator.designsystem.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Monta brand palette (Brand Guidelines v2.2).
 *
 * Primary Blue is the signature, ownable Monta colour and should always be the
 * most prominent. Cold (blues) and warm (coral/burgundy) families never mix —
 * everything here is cold + neutrals, so the UI stays on-brand by construction.
 */
object MontaColors {
    // Primary palette
    val Black = Color(0xFF000000)
    val Charcoal = Color(0xFF292929)
    val GreyBackground = Color(0xFFECECEC)
    val White = Color(0xFFFFFFFF)
    val PrimaryBlue = Color(0xFF302EE0) // signature colour — leads the UI

    // Secondary palette — cold family (used sparingly)
    val LightBlue = Color(0xFF337DFF)
    val DarkBlue = Color(0xFF001A3D)

    // Neutrals
    val DarkGrey = Color(0xFF4F4F4F)
    val MediumGrey = Color(0xFFA3A3A3)
    val LightGrey = Color(0xFFD4D4D4)

    val RichBlack = Color(0xFF09090B) // page background
    val SurfaceBlack = Color(0xFF0F0F12) // cards, dialogs, sheets
}
