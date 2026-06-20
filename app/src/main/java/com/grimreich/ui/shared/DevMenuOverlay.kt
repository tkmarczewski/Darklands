package com.grimreich.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.grimreich.ui.main.GameRootViewModel
import com.grimreich.ui.main.GameScreenMode

@Composable
fun DevMenuOverlay(
    root: GameRootViewModel,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        Text(
            text = if (visible) "[X]" else "[DEV]",
            color = if (visible) Color.Red else Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .zIndex(100f)
                .background(Color(0xCC000000))
                .clickable { visible = !visible }
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart).zIndex(99f)
        ) {
            Surface(
                color = Color(0xF0050505),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, start = 4.dp, end = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("DEV MENU", color = Color.Red)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { root.setMode(GameScreenMode.HUB); visible = false }) {
                            Text("HUB")
                        }
                        Button(onClick = { root.setMode(GameScreenMode.WORLD_MAP); visible = false }) {
                            Text("MAPA")
                        }
                        Button(onClick = { root.setMode(GameScreenMode.CITY); visible = false }) {
                            Text("MIASTO")
                        }
                        Button(onClick = { root.setMode(GameScreenMode.QUESTS); visible = false }) {
                            Text("QUESTY")
                        }
                        Button(onClick = {
                            val s = root.gameRepository.currentState()
                            s.gold += 500
                            root.saveGame()
                        }) {
                            Text("+500 GOLD")
                        }
                    }
                }
            }
        }
    }
}
