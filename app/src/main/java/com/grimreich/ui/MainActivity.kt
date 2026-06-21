package com.grimreich.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.ui.main.GameNavHost
import com.grimreich.ui.main.GameRootViewModel
import com.grimreich.ui.shared.DevMenuOverlay
import com.grimreich.core.GameBootstrapper
import com.grimreich.ui.theme.GrimTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrimTheme {
                val rootViewModel: GameRootViewModel = hiltViewModel()
                DevMenuOverlay(root = rootViewModel) {
                    GameNavHost(root = rootViewModel)
                }
            }
        }
    }
}
