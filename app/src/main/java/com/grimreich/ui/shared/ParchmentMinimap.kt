package com.grimreich.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.grimreich.R

/**
 * Diegetyczna Minimapa — fragment szarpanego pergaminu z widokiem świata.
 */
@Composable
fun ParchmentMinimap(
    modifier: Modifier = Modifier,
    locationName: String = "Schwarzwald"
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE5D3B3)) // Kolor starego papieru
            .border(2.dp, Color(0xFF5D4037), RoundedCornerShape(8.dp))
    ) {
        // Tło mapy (wycinek)
        Image(
            painter = painterResource(id = R.drawable.bg_world_map), // Używamy Twojego assetu bg_world_map
            contentDescription = "Minimap",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Runa Kotwicy (Twoja pozycja)
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red)
                .align(Alignment.Center)
        )
    }
}
