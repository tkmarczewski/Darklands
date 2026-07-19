package com.grimreich.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.grimreich.ui.main.GameRootViewModel
import com.grimreich.ui.main.GameScreenMode
import com.grimreich.ui.DevMenuScreen

@Composable
fun DevMenuOverlay(
    root: GameRootViewModel,
    content: @Composable () -> Unit
) {
    val mode by root.mode.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        if (mode != GameScreenMode.dev_menu) {
            // MNIEJSZY PRZYCISK DEV, PRZESUNIĘTY ABY NIE ZASŁANIAĆ NPC
            Surface(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
                    .size(60.dp)
                    .align(Alignment.TopStart)
                    .clickable { 
                        android.util.Log.e("TRIBUNAL", "!!! DEV CLICK DETECTED !!!")
                        root.setMode(GameScreenMode.dev_menu) 
                    },
                color = Color.Magenta.copy(alpha = 0.4f),
                shape = androidx.compose.foundation.shape.CircleShape,
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("DEV", color = Color.White, fontSize = 9.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black).zIndex(10f)) {
                DevMenuScreen(
                    root = root,
                    onBack = { root.setMode(com.grimreich.ui.main.GameScreenMode.hub) }
                )
            }
        }
    }
}
