package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.ui.main.GameNavHost
import com.grimreich.ui.main.GameRootViewModel
import com.grimreich.ui.shared.DevMenuOverlay
import com.grimreich.core.GameBootstrapper
import com.grimreich.core.LanguageManager
import com.grimreich.ui.theme.GrimTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : LocalizedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- IMMERSIVE MODE: Hide System Bars ---
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            GrimTheme {
                BackHandler { /* Disable system back button to enforce ontological stability */ }
                val rootViewModel: GameRootViewModel = hiltViewModel()
                DevMenuOverlay(root = rootViewModel) {
                    GameNavHost(root = rootViewModel)
                }
            }
        }
    }
}
