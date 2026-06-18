package com.grimreich.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grimreich.core.GameRepository
import com.grimreich.ui.main.GameNavHost
import com.grimreich.ui.main.GameRootViewModel
import com.grimreich.systems.GameLoopController
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestSystem
import com.grimreich.ui.theme.GrimTheme
import com.grimreich.world.CityCatalogue

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bootstrap if empty
        if (GameRepository.state.party.isEmpty()) {
            GameLoopController.bootstrap(seed = 1)
        }
        
        // MANDATORY SESSION SEEDING
        CityCatalogue.clear()
        CityCatalogue.seedCanonical()
        DialogueManager.seedBasicDialogues()
        QuestSystem.seedIntegratedContent(seed = 1)

        setContent {
            GrimTheme {
                val rootViewModel: GameRootViewModel = viewModel()
                GameNavHost(root = rootViewModel)
            }
        }
    }
}
