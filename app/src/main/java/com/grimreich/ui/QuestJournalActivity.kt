package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.QuestSystem

class QuestJournalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_journal)

        renderQuests()

        findViewById<Button>(R.id.btnExitJournal).setOnClickListener {
            finish()
        }
    }

    private fun renderQuests() {
        val container = findViewById<LinearLayout>(R.id.questListContainer)
        container.removeAllViews()

        val allQuests = QuestSystem.all()
        if (allQuests.isEmpty()) {
            val tv = TextView(this).apply {
                text = "Brak aktywnych zadań w tym pęknięciu świata."
                styleToGrim()
            }
            container.addView(tv)
            return
        }

        allQuests.forEach { quest ->
            val tv = TextView(this).apply {
                text = "• ${quest.title}\n  ${quest.description}\n  Status: ${quest.status}"
                styleToGrim()
                setPadding(24, 24, 24, 24)
            }
            container.addView(tv)
        }
    }

    private fun TextView.styleToGrim() {
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.grimTextPrimary))
        this.textSize = 14f
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        this.layoutParams = params
    }
}
