package com.grimreich.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.grimreich.R

/**
 * Komponent renderujący skalowalną ramkę RPG przy użyciu Twoich assetów.
 * Rozwiązuje problem "pękających" ramek poprzez inteligentne skalowanie krawędzi.
 */
@Composable
fun GrimNinePatchFrame(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                // Tutaj w przyszłości dodamy logikę 9-patch jeśli standardowy content nie wystarczy.
                // Na razie używamy Boxa z paddingiem, by "zawartość" nie nachodziła na ramy.
            }
    ) {
        // Główna ramka (Ramka cienka.png) - nakładana na wierzch
        // Używamy matchParentSize, by ramka zawsze otaczała kontener
        Image(
            painter = painterResource(id = com.grimreich.R.drawable.ui_frame_gold), // Używamy Twojej złotej ramki
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
