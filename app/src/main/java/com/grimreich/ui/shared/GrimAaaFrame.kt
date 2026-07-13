package com.grimreich.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.grimreich.R

/**
 * Komponent AAA Retro: Ciężka, rzeźbiona rama ze złota i kamienia.
 * Wykorzystuje narożniki i krawędzie dla efektu 3D.
 */
@Composable
fun GrimAaaFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        // Tło pergaminu (Wewnętrzne)
        Image(
            painter = painterResource(id = R.drawable.ui_panel_stats),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.9f
        )

        // Treść z dużym paddingiem na ramy
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            content()
        }

        // RAMA ZŁOTA (Na wierzchu)
        Image(
            painter = painterResource(id = R.drawable.ui_frame_gold),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}
