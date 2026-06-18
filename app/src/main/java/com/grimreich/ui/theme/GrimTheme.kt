package com.grimreich.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GrimGold = Color(0xFFE0C080)
val GrimRed = Color(0xFFB22222)
val GrimDarkBg = Color(0xFF0A0A0A)
val GrimSurface = Color(0xFF1A1A1A)

private val DarkColorScheme = darkColorScheme(
    primary = GrimGold,
    secondary = GrimRed,
    background = GrimDarkBg,
    surface = GrimSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun GrimTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
