package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.CombatSystem
import com.grimreich.systems.QuestSystem
import androidx.activity.compose.setContent
import com.grimreich.ui.combat.CombatScreen
import com.grimreich.ui.combat.CombatViewModel

class CombatActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (!GameRepository.state.combat.active) {
      val questId = GameRepository.state.pendingQuestId
      if (questId != null) {
        CombatSystem.startEncounterForQuest(questId)
      } else {
        CombatSystem.startRandomEncounter()
      }
    }

    val viewModel = CombatViewModel()

    setContent {
        CombatScreen(viewModel = viewModel, onExit = { finish() })
    }
  }
}
