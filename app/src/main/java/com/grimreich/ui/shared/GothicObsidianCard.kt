package com.grimreich.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Komponent bazy dla V9: Gothic Obsidian.
 * Czysta czerń, złoty obrys (podwójna linia), krwisty gradient w nagłówku.
 */
@Composable
fun GothicObsidianCard(
    modifier: Modifier = Modifier,
    headerColor: Color = Color(0xFF4A0000), // Krwista czerwień
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(Color.Black)
            .border(1.dp, Color(0xFFC0A060)) // Cienkie złoto
            .padding(1.dp)
            .border(0.5.dp, Color(0xFFC0A060)) // Efekt podwójnej linii
    ) {
        // Nagłówek z gradientem
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(headerColor, Color.Transparent)
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            content = content
        )
    }
}
