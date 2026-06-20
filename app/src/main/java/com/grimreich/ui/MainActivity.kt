package com.grimreich.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.grimreich.core.GameRepository
import com.grimreich.ui.main.GameNavHost
import com.grimreich.ui.main.GameRootViewModel
import com.grimreich.core.GameBootstrapper
import com.grimreich.ui.theme.GrimTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var gameRepository: GameRepository
    @Inject lateinit var gameBootstrapper: GameBootstrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bootstrap if session is missing but we're in MainActivity (should have been done in Creator)
        if (!gameRepository.hasSession()) {
            lifecycleScope.launch {
                gameBootstrapper.bootstrapFreshWorld(seed = 1)
            }
        }

        setContent {
            GrimTheme {
                val rootViewModel: GameRootViewModel = hiltViewModel()
                GameNavHost(root = rootViewModel)
            }
        }
    }
}
