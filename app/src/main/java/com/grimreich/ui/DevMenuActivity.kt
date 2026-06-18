package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestSystem

class DevMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(android.graphics.Color.BLACK)
        }

        layout.addView(devButton("RESET & START") {
            GameRepository.seed()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        })

        layout.addView(devButton("ADD 1000 GOLD") {
            GameRepository.state.gold += 1000
        })

        layout.addView(devButton("INSTANT ENDGAME") {
            instantEndgame()
        })

        layout.addView(devButton("POWRÓT") {
            finish()
        })

        setContentView(layout)
    }

    private fun devButton(txt: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = txt
            setOnClickListener { onClick() }
        }
    }

    private fun instantEndgame() {
        val state = GameRepository.state
        QuestSystem.seedIntegratedContent()
        
        // Complete some dummy quests to trigger endgame logic if any exists
        val first = QuestSystem.all().firstOrNull()?.id
        if (first != null) {
            QuestSystem.complete(first)
        }
        
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
