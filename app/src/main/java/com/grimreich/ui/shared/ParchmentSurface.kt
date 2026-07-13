package com.grimreich.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.grimreich.R

/**
 * Komponent nadający całemu interfejsowi teksturę mrocznego, starego pergaminu.
 */
@Composable
fun ParchmentSurface(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        // Tło bazowe z teksturą (używamy mapy jako tekstury papieru pod spodem)
        Image(
            painter = painterResource(id = R.drawable.bg_world_map),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f // Bardzo ciemne, tylko tekstura
        )
        
        // Dodatkowa warstwa brudu/mroku
        Box(modifier = Modifier.fillMaxSize().background(Color(0xAA000000)))
        
        content()
    }
}
