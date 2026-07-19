package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.ui.main.GameNavHost
import com.grimreich.ui.main.GameRootViewModel
import com.grimreich.ui.main.components.ExitConfirmationDialog
import com.grimreich.ui.shared.DevMenuOverlay
import com.grimreich.core.GameBootstrapper
import com.grimreich.core.LanguageManager
import com.grimreich.ui.theme.GrimTheme
import com.grimreich.systems.AudioEngine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : LocalizedActivity() {
    @Inject lateinit var audioEngine: AudioEngine
    @Inject lateinit var gameRepository: com.grimreich.core.GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- IMMERSIVE MODE: Hide System Bars ---
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            GrimTheme {
                val rootViewModel: GameRootViewModel = hiltViewModel()
                val mode = rootViewModel.mode.collectAsState().value
                
                val showExitDialog = rootViewModel.showExitConfirmation.collectAsState().value

                if (showExitDialog) {
                    ExitConfirmationDialog(
                        onConfirm = { rootViewModel.confirmExitToMainMenu() },
                        onDismiss = { rootViewModel.setExitConfirmationVisible(false) }
                    )
                }

                // --- SYSTEM BACK HANDLER ---
                // If we are in Main Menu, let the system handle it (exit app).
                // Otherwise, show confirmation dialog.
                BackHandler(enabled = mode != com.grimreich.ui.main.GameScreenMode.main_menu) {
                    android.util.Log.d("TRIBUNAL", "Back action: requesting exit confirmation.")
                    rootViewModel.setExitConfirmationVisible(true)
                }

                DevMenuOverlay(root = rootViewModel) {
                    GameNavHost(root = rootViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        audioEngine.release()
        gameRepository.close()
        super.onDestroy()
    }
}
