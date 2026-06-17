package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import androidx.activity.compose.setContent
import com.grimreich.ui.dialogue.DialogueScreen
import com.grimreich.ui.dialogue.DialogueViewModel

class DialogueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val npcName = intent.getStringExtra("npcName") ?: "Nieznajomy"
        val npcRole = intent.getStringExtra("npcRole") ?: "Echo"
        val startNodeId = intent.getStringExtra("startNodeId") ?: "end"

        val viewModel = DialogueViewModel()
        viewModel.init(npcName, npcRole, startNodeId)

        setContent {
            DialogueScreen(viewModel = viewModel, onExit = { finish() })
        }
    }
}
